package com.hp.bingo.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hp.bingo.constants.AppConstant;
import com.hp.bingo.dto.Request;
import com.hp.bingo.dto.Response;
import com.hp.bingo.entities.EntryForm;
import com.hp.bingo.repo.EntryFormRepository;
import com.hp.bingo.service.mail.MailTemplates;
import com.hp.bingo.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final ModelMapper modelMapper;
    private final Utils utils;
    private final EntryFormRepository entryFormRepository;
    private final MailTemplates emailService;
    private final Cloudinary cloudinary;


    @Value("${app.payment.per-ticket-price}")
    private int perTicketPrice;
    @Value("${app.payment.upi}")
    private String upiId;


    private final Map<String, Request> pendingRegistrations = new ConcurrentHashMap<>();

    public Response register(Request request) {
        log.info("Registering new entry: {}", request);

        // === Validation ===
        if (request == null)
            return utils.errorResponse("Request cannot be null");
        if (utils.isBlank(request.getName()) || request.getName().length() < 2)
            return utils.errorResponse("Name must be at least 2 characters");
        if (!utils.isValidPhone(request.getPhone()))
            return utils.errorResponse("Invalid phone number format");
        if (!utils.isValidEmail(request.getEmail()))
            return utils.errorResponse("Invalid email format");
        if (request.getTickets() <= 0)
            return utils.errorResponse("Tickets must be greater than zero");

        long existingCount = entryFormRepository.countByPhoneOrEmail(request.getPhone(), request.getEmail());
        if (existingCount >= 5)
            return utils.errorResponse("You can only register up to 5 times with the same phone or email");

        String key = buildKey(request.getEmail(), request.getPhone());

        // === If Pay Now ===
        if (request.isPayNow()) {
            pendingRegistrations.put(key, request); // store in memory until proof upload
            int amount = request.getTickets() * perTicketPrice;
            String payeeName = "Ganapati Festival";
            String note = "Housie Ticket Payment";

            String upiUri = String.format(
                    "upi://pay?pa=%s&pn=%s&am=%d&cu=INR&tn=%s",
                    upiId,
                    URLEncoder.encode(payeeName, StandardCharsets.UTF_8),
                    amount,
                    URLEncoder.encode(note, StandardCharsets.UTF_8));

            String qrBase64 = generateQrBase64(upiUri);

            log.info("PayNow flow initiated for {}", key);

            return Response.builder()
                    .msg("Registration initiated. Please complete payment and upload proof.")
                    .status(AppConstant.SUCCESS)
                    .data(Map.of(
                            "paymentAmount", amount,
                            "upiUri", upiUri,
                            "paymentQrBase64", qrBase64))
                    .build();
        }

        // === If Pay Later === → save directly in DB
        EntryForm entryForm = modelMapper.map(request, EntryForm.class);
        entryForm.setCreatedAt(Instant.now());
        entryForm.setPaymentConfirmed(false);
        entryForm.setRegistrationId(generateUniqueRegistrationId());
        entryForm.setImagePath(null);
        entryForm.setStatus("1");

        try {
            entryFormRepository.save(entryForm);
            log.info("PayLater registration saved: {}", entryForm.getRegistrationId());

            return Response.builder()
                    .msg("Registration completed successfully! You can pay at the venue.")
                    .status(AppConstant.SUCCESS)
                    .data(Map.of("registrationId", entryForm.getRegistrationId()))
                    .build();
        } catch (Exception e) {
            log.error("Error saving registration", e);
            return utils.errorResponse("Error saving registration");
        }
    }

    public Response addPaymentProof(String email, String phone, MultipartFile file) {
        if (file == null || file.isEmpty())
            return utils.errorResponse("Invalid file");

        String key = buildKey(email, phone);
        Request pending = pendingRegistrations.get(key);

        if (pending == null)
            return utils.errorResponse("No pending registration found for provided email/phone");

        try {
            String filePath = uploadImage(file);

            EntryForm entryForm = modelMapper.map(pending, EntryForm.class);
            entryForm.setCreatedAt(Instant.now());
            entryForm.setPaymentConfirmed(false);
            entryForm.setImagePath(filePath);
            entryForm.setRegistrationId(generateUniqueRegistrationId()); // 🔹 New ID
            entryForm.setStatus("1");
            entryFormRepository.save(entryForm);
            pendingRegistrations.remove(key);

            log.info("Payment proof saved for {}, registrationId={}", key, entryForm.getRegistrationId());

            emailService.sendGanapatiRegistrationEmail(entryForm);

            return Response.builder()
                    .msg("Payment proof uploaded successfully. Awaiting admin confirmation.")
                    .status(AppConstant.SUCCESS)
                    .data(Map.of(
                            "storedPath", filePath,
                            "registrationId", entryForm.getRegistrationId()))
                    .build();

        } catch (Exception e) {
            log.error("Error saving payment proof for {}", key, e);
            return utils.errorResponse("Error saving payment proof");
        }
    }

    public Response getRegistrationDetails(String registrationId) {
        if (utils.isBlank(registrationId))
            return utils.errorResponse("Registration ID cannot be empty");

        EntryForm entryForm = entryFormRepository.findByRegistrationId(registrationId)
                .orElse(null);

        if (entryForm == null)
            return utils.errorResponse("No registration found for ID: " + registrationId);

        return Response.builder()
                .status(AppConstant.SUCCESS)
                .msg("Registration details retrieved successfully")
                .data(entryForm)
                .build();
    }

    private String buildKey(String email, String phone) {
        return email.trim().toLowerCase() + "-" + phone.trim();
    }

    // private String storeFileLocally(MultipartFile file) throws IOException {
    //     String uploadDir = System.getProperty("user.dir") + "/uploads";
    //     File dir = new File(uploadDir);
    //     if (!dir.exists())
    //         dir.mkdirs();

    //     String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
    //     File destination = new File(dir, fileName);
    //     file.transferTo(destination);

    //     log.info("File stored locally at {}", destination.getAbsolutePath());
    //     return destination.getAbsolutePath();
    // }

    private String generateUniqueRegistrationId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new SecureRandom();

        String registrationId;
        boolean exists;

        do {
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            registrationId = sb.toString();
            exists = entryFormRepository.existsByRegistrationId(registrationId);
        } while (exists);

        return registrationId;
    }

    /**
     * Generates Base64 PNG QR from text
     */
    private String generateQrBase64(String text) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);
        } catch (Exception e) {
            log.error("Error generating QR", e);
            return null;
        }

    }

    
    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", "myapp/images"));
        return uploadResult.get("secure_url").toString(); // CDN URL
    }

    
}
