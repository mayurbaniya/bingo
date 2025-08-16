package com.hp.bingo.service.mail;

import com.hp.bingo.entities.EntryForm;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class GanpatiTicketGenerator {

    public static ByteArrayOutputStream generateTicket(EntryForm entryForm) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        
        // Custom ticket size (standard ticket size)
        PageSize customSize = new PageSize(400, 600);
        Document document = new Document(pdf, customSize);
        document.setMargins(20, 20, 20, 20);
        
        // Load fonts
        PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont regularFont = PdfFontFactory.createFont("Helvetica");
        PdfFont decorativeFont = PdfFontFactory.createFont("Helvetica-BoldOblique");
        
        // Colors
        DeviceRgb saffron = new DeviceRgb(255, 102, 0);
        DeviceRgb gold = new DeviceRgb(255, 153, 51);
        DeviceRgb lightGold = new DeviceRgb(245, 231, 161);
        DeviceRgb darkGreen = new DeviceRgb(19, 136, 8); // Indian green

        // Background pattern
        InputStream bgStream = GanpatiTicketGenerator.class.getResourceAsStream("/static/gallery/ganpatibg.png");
        ImageData patternData = ImageDataFactory.create(bgStream.readAllBytes());
        Image pattern = new Image(patternData);
        pattern.setOpacity(0.1f);
        pattern.setFixedPosition(0, 0);
        pattern.scaleToFit(customSize.getWidth(), customSize.getHeight());
        document.add(pattern);
        
        // Header with Ganpati image
        InputStream iconStream = GanpatiTicketGenerator.class.getResourceAsStream("/static/gallery/ganpatiheader.png");
        ImageData ganpatiData = ImageDataFactory.create(iconStream.readAllBytes());
        Image ganpatiImg = new Image(ganpatiData);
        ganpatiImg.setWidth(100);
        ganpatiImg.setHorizontalAlignment(HorizontalAlignment.CENTER);
        ganpatiImg.setMarginBottom(10);
        document.add(ganpatiImg);
        
        // Title with decorative border
        Div titleContainer = new Div()
            .setBorder(new SolidBorder(gold, 2))
            .setBorderRadius(new BorderRadius(10))
            .setPadding(10)
            .setBackgroundColor(new DeviceRgb(255, 255, 255))
            .setOpacity(0.8f)
            .setMarginBottom(15)
            .setHorizontalAlignment(HorizontalAlignment.CENTER);
        
        Paragraph title = new Paragraph("Ganapati Festival Pass")
            .setFont(boldFont)
            .setFontSize(24)
            .setFontColor(saffron)
            .setTextAlignment(TextAlignment.CENTER);
        titleContainer.add(title);
        document.add(titleContainer);
        
        // Confirmation message
        Paragraph confirmation = new Paragraph("🎉 Payment Confirmed! 🎉")
            .setFont(decorativeFont)
            .setFontSize(18)
            .setFontColor(darkGreen)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(15);
        document.add(confirmation);
        
        // Registration ID
        Paragraph regId = new Paragraph("Registration ID: " + entryForm.getRegistrationId())
            .setFont(boldFont)
            .setFontSize(14)
            .setFontColor(ColorConstants.DARK_GRAY)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20);
        document.add(regId);
        
        // Main content container
        Div contentBox = new Div()
            .setBorder(new SolidBorder(gold, 1))
            .setBorderRadius(new BorderRadius(15))
            .setPadding(20)
            .setBackgroundColor(new DeviceRgb(255, 255, 255))
            .setOpacity(0.9f)
            .setMarginBottom(20);
        
        // Ticket holder section
        Div ticketHolder = new Div()
            .setMarginBottom(15);
        ticketHolder.add(new Paragraph("Ticket Holder").setFont(boldFont).setFontSize(16).setMarginBottom(5));
        ticketHolder.add(new Paragraph(entryForm.getName()).setFont(regularFont).setFontSize(16).setFontColor(saffron));
        contentBox.add(ticketHolder);
        
        // Decorative separator
        SolidLine separatorLine = new SolidLine(1f);
        separatorLine.setColor(lightGold);
        contentBox.add(new LineSeparator(separatorLine).setMarginTop(10).setMarginBottom(15));
        
        // Ticket details table
        float[] columnWidths = {1, 2};
        Table detailsTable = new Table(UnitValue.createPercentArray(columnWidths))
            .setWidth(UnitValue.createPercentValue(100))
            .setMarginBottom(15);
        
        detailsTable.addCell(createCell("Tickets:", boldFont, true));
        detailsTable.addCell(createCell(String.valueOf(entryForm.getTickets()), regularFont, false));
        detailsTable.addCell(createCell("Phone:", boldFont, true));
        detailsTable.addCell(createCell(entryForm.getPhone(), regularFont, false));
        detailsTable.addCell(createCell("Email:", boldFont, true));
        detailsTable.addCell(createCell(entryForm.getEmail(), regularFont, false));
        detailsTable.addCell(createCell("Amount Paid:", boldFont, true));
        detailsTable.addCell(createCell("₹" + entryForm.getAmountPaid(), regularFont, false));
        contentBox.add(detailsTable);
        
        // Event details
        Div eventBox = new Div()
            .setBackgroundColor(lightGold)
            .setBorderRadius(new BorderRadius(10))
            .setPadding(15)
            .setMarginTop(10);
        
        eventBox.add(new Paragraph("🎉 Event Details").setFont(boldFont).setFontSize(16).setMarginBottom(10));
        eventBox.add(new Paragraph("📅 15th September, 7 PM onwards").setFont(regularFont).setMarginBottom(5));
        eventBox.add(new Paragraph("🏛️ Ganapati Hall, Main Street").setFont(regularFont));
        contentBox.add(eventBox);
        
        document.add(contentBox);
        
        // Footer with sacred symbol
        Div footer = new Div()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(10);
        
        // Add Om symbol
        Paragraph omSymbol = new Paragraph("ॐ")
            .setFontSize(36)
            .setFontColor(gold)
            .setMarginBottom(5);
        footer.add(omSymbol);
        
        // Footer note
        footer.add(new Paragraph("Present this ticket at the venue for entry")
            .setFont(regularFont)
            .setFontSize(12)
            .setMarginBottom(3));
        footer.add(new Paragraph("Ganpati Bappa Morya!")
            .setFont(boldFont)
            .setFontSize(14)
            .setFontColor(saffron));
        
        document.add(footer);
        
        // Decorative border at bottom
        SolidLine bottomLine = new SolidLine(2);
        bottomLine.setColor(gold);
        document.add(new LineSeparator(bottomLine).setMarginTop(15));

        document.close();
        return baos;
    }

    private static Cell createCell(String text, PdfFont font, boolean isHeader) {
        Cell cell = new Cell().add(new Paragraph(text).setFont(font));
        cell.setPadding(5);
        cell.setBorder(Border.NO_BORDER);
        
        if (isHeader) {
            cell.setBackgroundColor(new DeviceRgb(245, 231, 161));
        }
        return cell;
    }
}