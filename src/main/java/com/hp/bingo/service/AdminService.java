package com.hp.bingo.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hp.bingo.constants.AppConstant;
import com.hp.bingo.dto.EventRequest;
import com.hp.bingo.dto.Response;
import com.hp.bingo.entities.EntryForm;
import com.hp.bingo.entities.Events;
import com.hp.bingo.repo.EntryFormRepository;
import com.hp.bingo.repo.EventsRepository;
import com.hp.bingo.service.mail.MailTemplates;
import com.hp.bingo.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final EntryFormRepository entryFormRepository;
    private final Utils utils;
    private final MailTemplates emailService;
    private final ModelMapper mapper;
    private final EventsRepository eventsRepository;

    public Response getAllRegistrations(Boolean paymentConfirmed, int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        long totalRegistrations;
        long paymentConfirmedCount;
        long paymentPendingCount;
        long totalConfirmedAmount;

        Page<EntryForm> formsPage;
        if (paymentConfirmed != null) {
            if (!status.equals("1") && !status.equals("0")) {
                return utils.errorResponse("Invalid status parameter. Use '1' for Active or '0' for Deleted.");
            }

            totalRegistrations = entryFormRepository.getTotalRegistrations();
            paymentConfirmedCount = entryFormRepository.getPaymentConfirmedCount();
            paymentPendingCount = entryFormRepository.getPaymentPendingCount();
            totalConfirmedAmount = entryFormRepository.getTotalConfirmedAmount();

            formsPage = entryFormRepository.findByPaymentConfirmedAndStatus(paymentConfirmed, pageable, status);
        } else {
            if (!status.equals("1") && !status.equals("0")) {
                return utils.errorResponse("Invalid status parameter. Use '1' for Active or '0' for Deleted.");
            }

            totalRegistrations = entryFormRepository.getTotalRegistrations();
            paymentConfirmedCount = entryFormRepository.getPaymentConfirmedCount();
            paymentPendingCount = entryFormRepository.getPaymentPendingCount();
            totalConfirmedAmount = entryFormRepository.getTotalConfirmedAmount();

            formsPage = entryFormRepository.findAllByStatus(pageable, status);
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("registrations", formsPage.getContent());
        responseData.put("currentPage", formsPage.getNumber());
        responseData.put("totalItems", formsPage.getTotalElements());
        responseData.put("totalPages", formsPage.getTotalPages());
        responseData.put("totalRegistrations", totalRegistrations);
        responseData.put("paymentConfirmedCount", paymentConfirmedCount);
        responseData.put("paymentPendingCount", paymentPendingCount);
        responseData.put("totalConfirmedAmount", totalConfirmedAmount);


        System.out.println("total payment -> "+ totalConfirmedAmount);

        return Response.builder()
                .status(AppConstant.SUCCESS)
                .msg("Registrations fetched successfully")
                .data(responseData)
                .build();
    }

    public Response confirmPayment(Long id) {
        EntryForm entryForm = entryFormRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));

        entryForm.setPaymentConfirmed(true);
        entryFormRepository.save(entryForm);

        log.info("Payment confirmed for ID {}", id);

        emailService.sendPaymentConfirmationEmail(entryForm);
        return Response.builder()
                .status(AppConstant.SUCCESS)
                .msg("Payment confirmed successfully")
                .build();
    }



    @SuppressWarnings("deprecation")
    public Response deleteRegistration(Long id) {
        if (!entryFormRepository.existsById(id)) {
            return utils.errorResponse("Registration not found");
        }

        EntryForm entryForm = entryFormRepository.getById(id);
        entryForm.setStatus("0");
        entryFormRepository.save(entryForm);

        log.info("Deleted registration with ID {}", id);

        return Response.builder()
                .status(AppConstant.SUCCESS)
                .msg("Registration deleted successfully")
                .build();
    }

    public ResponseEntity<?> getPaymentProof(Long id) {
        EntryForm entryForm = entryFormRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));

        if (entryForm.getImagePath() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No payment proof available for this entry");
        }

        File file = new File(entryForm.getImagePath());
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("File not found on server");
        }

        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            log.error("Error reading file {}", file.getAbsolutePath(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving payment proof");
        }
    }

    public Response searchRegistrations(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<EntryForm> formsPage = entryFormRepository.search(searchTerm, pageable);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("registrations", formsPage.getContent());
        responseData.put("currentPage", formsPage.getNumber());
        responseData.put("totalItems", formsPage.getTotalElements());
        responseData.put("totalPages", formsPage.getTotalPages());

        return Response.builder()
                .status(AppConstant.SUCCESS)
                .msg("Search completed successfully")
                .data(responseData)
                .build();
    }

    public Response addEvent(EventRequest request) {
        try {

            Events event = mapper.map(request, Events.class);
            eventsRepository.save(event);
            return Response.builder()
                    .status(AppConstant.SUCCESS)
                    .msg("Event added successfully")
                    .build();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return Response.builder().build();
    }


    public Response getConfirmedUserList(){
         try {

            List<EntryForm> paymentConfirmedtList = entryFormRepository.findByPaymentConfirmedTrueAndStatus("1");
            return Response.builder()
                    .data(paymentConfirmedtList)
                    .status(AppConstant.SUCCESS)
                    .msg("Fetched Confirmed users successfully")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.builder()
            .err(e.getLocalizedMessage())
            .msg("Failed to fetch confirmed users!!")
            .status(AppConstant.FAILED).build();
        }
        
    }
}
