package com.hp.bingo.service.mail;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hp.bingo.entities.EntryForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailTemplates {

    private final MailService emailService;

    @Value("${mail.admin}")
    private String adminMail;
    @Value("${mail.dev}")
    private String devMail;
    @Value("${app.baseUrl}")
    private String baseUrl;

    private String ganpatiLogoUrl=	"https://res.cloudinary.com/djrnih208/image/upload/v1755936458/myapp/images/xrk6o8xsgkzfkyn6ml06.png";

    public boolean sendGanapatiRegistrationEmail(EntryForm entryForm) {
        String subject = "🎉 Registration Confirmed | Ganapati Housie Festival";

        String body = "<html>" +
                "<head>" +
                "<style>" +
                "   .hero {background: linear-gradient(135deg, #f39c12 0%, #d35400 100%); padding: 30px; text-align: center;}"
                +
                "   .cta-button {background-color: #27ae60; color: white; padding: 12px 25px; border-radius: 25px; text-decoration: none; display: inline-block; font-weight: bold;}"
                +
                "   .cta-button:hover {background-color: #2ecc71;}" +
                "</style>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; margin: 0;'>" +
                "<div class='hero'>" +
                "<img src='" + ganpatiLogoUrl + "' alt='Ganapati Logo' style='height: 60px; margin-bottom: 20px;'>"
                +
                "<h1 style='color: white; margin: 0; font-size: 28px;'>🙏 Registration Successful, "
                + entryForm.getName() + "!</h1>" +
                "<p style='color: #fcefe3; font-size: 20px; margin: 10px 0 0;'>You’re all set for the Housie Game Night 🎲</p>"
                +
                "</div>" +
                "<div style='padding: 30px 20px;'>" +
                "<div style='text-align: center; margin: 20px 0;'>" +
                "<div style='font-size: 50px; color: #e67e22; margin-bottom: 15px;'>📝</div>" +
                "<p style='font-size: 18px; color: #2c3e50;'>We’ve received your registration. Here are the details:</p>"
                +
                "</div>" +
                "<div style='background: #f8f9fa; border-radius: 15px; padding: 20px; margin: 25px 0;'>" +
                "<h3 style='color: #d35400; margin-top: 0;'>📍 Registration Details:</h3>" +
                "<ul style='columns: 2; list-style: none; padding: 0;'>" +
                "<li style='margin-bottom: 8px;'>👤 Name: " + entryForm.getName() + "</li>" +
                "<li style='margin-bottom: 8px;'>📱 Phone: " + entryForm.getPhone() + "</li>" +
                "<li style='margin-bottom: 8px;'>📧 Email: " + entryForm.getEmail() + "</li>" +
                "<li style='margin-bottom: 8px;'>🎟 Tickets: " + entryForm.getTickets() + "</li>" +
                "<li style='margin-bottom: 8px;'>💰 Amount Paid: ₹" + (entryForm.getAmountPaid()) + "</li>" +
                "</ul>" +
                "</div>" +
                "<div style='background: #e8f5e9; border-radius: 15px; padding: 20px; margin: 25px 0;'>" +
                "<h4 style='color: #27ae60; margin: 0 0 15px 0;'>📷 Payment Proof:</h4>" +
                "<p style='margin: 0; color: #2c3e50;'>You can view your submitted payment proof here:</p>" +
                "<div style='text-align: center; margin-top: 15px;'>" +
                "<a class='cta-button' href='" + entryForm.getImagePath() + "'>View Payment Proof</a>" +
                "</div>" +
                "</div>" +
                "<div style='border-top: 3px solid #e67e22; margin: 30px 0; padding-top: 20px; text-align: center;'>" +
                "<p style='margin: 0; font-size: 14px; color: #7f8c8d;'>We will verify your payment soon and confirm your seat ✅</p>"
                +
                "<p style='margin: 10px 0 0; font-weight: bold; color: #2c3e50;'>Ganapati Festival Committee<br>🎉 See you at the event!</p>"
                +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        return emailService.sendMIMEMail(entryForm.getEmail(), subject, body, false);
    }


    public boolean sendPaymentConfirmationEmail(EntryForm entryForm) {
    try {
        // Generate PDF ticket
        ByteArrayOutputStream ticketPdf = GanpatiFestivalPassGenerator.generateTicket(entryForm);
        
        // Create email with attachment
        String subject = "✅ Your Ganapati Housie Pass & Payment Confirmation";
        
        String body = "<html>" +
                        "<head>" +
                        "<style>" +
                        "   .hero {background: linear-gradient(135deg, #ff6600 0%, #ff9933 100%); padding: 30px; text-align: center;}" +
                        "   .cta-button {background-color: #138808; color: white; padding: 12px 25px; border-radius: 25px; text-decoration: none; display: inline-block; font-weight: bold;}" +
                        "   .cta-button:hover {background-color: #0d6e07;}" +
                        "   .attachment-note {background: #fff3cd; border-left: 4px solid #ff9933; padding: 15px; margin: 20px 0; border-radius: 0 8px 8px 0;}" +
                        "   .ticket-icon {font-size: 24px; vertical-align: middle; margin-right: 10px;}" +
                        "</style>" +
                        "</head>" +
                        "<body style='font-family: Arial, sans-serif; line-height: 1.6; margin: 0;'>" +
                        "<div class='hero'>" +
                        "<img src='" + ganpatiLogoUrl + "' alt='Ganapati Logo' style='height: 60px; margin-bottom: 20px;'>" +
                        "<h1 style='color: white; margin: 0; font-size: 28px;'>🎉 Payment Confirmed, " + entryForm.getName() + "!</h1>" +
                        "<p style='color: #f5e7a1; font-size: 20px; margin: 10px 0 0;'>You're officially part of the festival night! Ganpati Bappa Morya! 🎊</p>" +
                        "</div>" +

                        "<div style='padding: 30px 20px;'>" +
                        "<div style='text-align: center; margin: 10px 0;'>" +
                        "<div style='font-size: 50px; color: #27ae60; margin-bottom: 10px;'>🎫</div>" +
                        "<p style='font-size: 18px; color: #2c3e50;'>Your payment has been verified and confirmed</p>" +
                        "</div>" +

                        "<!-- Attachment Note -->" +
                        "<div class='attachment-note'>" +
                        "<p style='margin: 0; color: #2c3e50;'><span class='ticket-icon'>📎</span> <strong>Your Festival Pass is Attached!</strong></p>" +
                        "<p style='margin: 10px 0 0; color: #2c3e50;'>Find your <strong>Ganapati Festival Pass</strong> attached to this email. Present it at the venue for entry.</p>" +
                        "</div>" +


                        "<div style='background: #fff3cd; border-radius: 15px; padding: 20px; margin: 25px 0;'>" +
                        "<h4 style='color: #d35400; margin: 0 0 15px 0;'>📅 Event Details:</h4>" +
                        "<p style='margin: 0; color: #2c3e50;'>Date: 15th September, 7 PM onwards</p>" +
                        "<p style='margin: 0; color: #2c3e50;'>Venue: Ganapati Hall, Main Street</p>" +
                        "<div style='text-align: center; margin-top: 15px;'>" +
                        "<a class='cta-button' href='https://maps.app.goo.gl/oGxitcTnEwHjdZZq7'>View Location</a>" +
                        "</div>" +
                        "</div>" +

                        "<div style='border-top: 3px solid #ff6600; margin: 30px 0; padding-top: 20px; text-align: center;'>" +
                        "<p style='margin: 0; font-size: 14px; color: #7f8c8d;'>Present your attached pass at the venue for entry ✅</p>" +
                        "<p style='margin: 10px 0 0; font-weight: bold; color: #2c3e50;'>Ganapati Festival Committee</p>" +
                        "<p style='margin: 5px 0 0; font-size: 18px; color: #ff9933; font-weight: bold;'>Ganpati Bappa Morya!</p>" +
                        "</div>" +
                        "</div>" +
                        "</body>" +
                        "</html>";
        
        // Send email with attachment
        return emailService.sendMIMEMailWithAttachment(
            entryForm.getEmail(),
            subject,
            body,
            ticketPdf.toByteArray(),
            "Ganapati_Festival_Pass.pdf", false
        );
    } catch (Exception e) {
        log.error("Error generating ticket: {}", e.getMessage());
        return false;
    }
}

}