package com.hp.bingo.service.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmailId;
    @Value("${mail.admin}")
    private String adminMail;
    @Value("${mail.admin2}")
    private String adminMail2;
    @Value("${mail.dev}")
    private String devMail;


    public boolean sendMail(String to, String subject, String body, boolean disableCC) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmailId);
        message.setTo(to);
        message.setCc(adminMail);
        message.setBcc(devMail);
        message.setSubject(subject);
        message.setText(body);

        try {
            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error("Error while sending mail : {} ", e.getMessage());
            return false;
        }
    }

    public boolean sendMIMEMail(String to, String subject, String body, boolean disableCC) {
        try {

            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmailId);
            helper.setTo(to);
            helper.setCc(disableCC ? "abc89328@test.example" : adminMail);
            helper.setCc(disableCC ? "abc89328@test.example" : devMail);
            helper.setCc(disableCC ? "abc89328@test.example" : adminMail2);
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(message);

            return true;

        } catch (Exception e) {

            log.error("Error while sending mail: {}", e.getMessage());
            return false;
        }

    }

    public boolean sendMIMEMailWithAttachment(String to, String subject, String body,
            byte[] attachment, String attachmentName, boolean disableCC) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmailId);
            helper.setTo(to);
            helper.setCc(disableCC ? "abc89328@test.example" : adminMail);
            helper.setCc(disableCC ? "abc89328@test.example" : devMail);
            helper.setCc(disableCC ? "abc89328@test.example" : adminMail2);
            helper.setSubject(subject);
            helper.setText(body, true);

            // Add PDF attachment
            helper.addAttachment(attachmentName, new ByteArrayResource(attachment));

            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage());
            return false;
        }
    }
}
