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
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


public class GanpatiFestivalPassGenerator {

 

//     public static void main(String[] args) {
//         System.out.println("Generating Ganpati Festival Pass...");

//         // Create sample data
//         EntryForm form = new EntryForm();
//         form.setName("Amit Sharma");
//         form.setTickets(4);
//         form.setPhone("9876543210");
//         form.setEmail("amit.sharma@example.com");
//         form.setRegistrationId("GANPATI-2025-12345");
//         form.setAmountPaid(800); // Assuming ₹200 per ticket

//         try {
//             //     ByteArrayOutputStream baos = generateFestivalPass(form);
//             ByteArrayOutputStream baos = generateTicket(form);

//             // Save to file for testing
//             java.nio.file.Files.write(
//                     java.nio.file.Paths.get("Ganpati_Premium_Pass.pdf"),
//                     baos.toByteArray()
//             );

//             System.out.println("Successfully generated: Ganpati_Premium_Pass.pdf");
//         } catch (Exception e) {
//             System.err.println("Error generating pass:");
//             e.printStackTrace();
//         }
//     }

    public static ByteArrayOutputStream generateTicket(EntryForm entryForm) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        
        // Use a slightly larger page size to avoid two pages
        PageSize pageSize = new PageSize(pdf.getDefaultPageSize().getWidth(), pdf.getDefaultPageSize().getHeight());
        Document document = new Document(pdf, pageSize);
        document.setMargins(30, 30, 30, 30);
        
        // Load fonts
        PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont regularFont = PdfFontFactory.createFont("Helvetica");
        
        // Modern color palette
        DeviceRgb primaryColor = new DeviceRgb(255, 107, 53);  // Vibrant orange
        DeviceRgb darkColor = new DeviceRgb(44, 62, 80);       // Dark blue-gray
        DeviceRgb lightBg = new DeviceRgb(248, 249, 250);      // Light gray backgroun

  
        
        // Create a light background
        Div background = new Div();
        background.setBackgroundColor(lightBg);
        background.setHeight(pageSize.getHeight());
        background.setWidth(pageSize.getWidth());
        // background.setFixedPosition(0, 0);
        background.setFixedPosition(1, pageSize.getLeft(),pageSize.getBottom(),pageSize.getWidth());

        document.add(background);

        InputStream bgStream = GanpatiFestivalPassGenerator.class.getResourceAsStream("/static/gallery/ganpatibg.png");
        ImageData patternData = ImageDataFactory.create(bgStream.readAllBytes());
        Image pattern = new Image(patternData);
        pattern.setOpacity(0.1f);
        pattern.setFixedPosition(115, -40);
        pattern.scaleToFit(pageSize.getWidth()-230, pageSize.getHeight()-230);
        document.add(pattern);
        
        // Header section
        Div header = new Div();
        header.setBackgroundColor(primaryColor);
        header.setPadding(0);
        header.setMarginTop(-30);
        header.setMarginLeft(-30);
        header.setMarginRight(-30);
        header.setMarginBottom(10);
        
        header.setTextAlignment(TextAlignment.CENTER);

        
        Paragraph title = new Paragraph("GANAPATI HOUSIE PASS")
            .setFont(boldFont)
            .setFontSize(20)
            .setFontColor(ColorConstants.WHITE)
            .setMarginBottom(2);
        
        // Paragraph subtitle = new Paragraph("Payment Confirmed!")
        //     .setFont(regularFont)
        //     .setFontSize(14)
        //     .setFontColor(ColorConstants.WHITE);
        
        header.add(title);
        // header.add(subtitle);
        document.add(header);
        
        // Registration ID
        Paragraph regId = new Paragraph("Registration ID: " + entryForm.getRegistrationId())
            .setFont(regularFont)
            .setFontSize(14)
            .setFont(boldFont)
            .setFontColor(darkColor)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20);
        document.add(regId);
        
        // Main content container
        Div contentBox = new Div()
            .setPadding(20)
            .setBackgroundColor(ColorConstants.WHITE)
            .setMarginBottom(20);
        
        // Ticket holder section
        Paragraph holderLabel = new Paragraph("TICKET HOLDER")
            .setFont(boldFont)
            .setFontSize(14)
            .setFontColor(darkColor)
            .setMarginBottom(5);
        
        Paragraph holderName = new Paragraph(entryForm.getName())
            .setFont(boldFont)
            .setFontSize(20)
            .setFontColor(primaryColor)
            .setMarginBottom(15);
        
        contentBox.add(holderLabel);
        contentBox.add(holderName);
        
        // Ticket details table
        float[] columnWidths = {40, 60};
        Table detailsTable = new Table(UnitValue.createPercentArray(columnWidths));
        detailsTable.setWidth(UnitValue.createPercentValue(100));
        detailsTable.setMarginBottom(15);
        
        addTableRow(detailsTable, "Tickets:", String.valueOf(entryForm.getTickets()), boldFont, regularFont);
        addTableRow(detailsTable, "Phone:", entryForm.getPhone(), boldFont, regularFont);
        addTableRow(detailsTable, "Email:", entryForm.getEmail(), boldFont, regularFont);
        addTableRow(detailsTable, "Amount Paid:", "Rs " + entryForm.getAmountPaid(), boldFont, regularFont);
        
        contentBox.add(detailsTable);
        
        // Event details
        Div eventBox = new Div()
            .setBackgroundColor(lightBg)
            .setPadding(15)
            .setMarginTop(10);
        
        eventBox.add(new Paragraph("Event Details")
            .setFont(boldFont)
            .setFontSize(15)
            .setFontColor(darkColor)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(10));
        
        eventBox.add(new Paragraph("📅 15th September, 7 PM onwards")
            .setFont(regularFont)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(5));
        
        eventBox.add(new Paragraph("🏛️ Ganapati Hall, Main Street")
            .setFont(regularFont)
            .setTextAlignment(TextAlignment.CENTER));
        
        contentBox.add(eventBox);
        document.add(contentBox);
        
        // Footer
        Div footer = new Div()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(10);
        
        footer.add(new Paragraph("Present this ticket at the venue for entry")
            .setFont(boldFont)
            .setFontSize(15)
            .setFontColor(darkColor)
            .setMarginBottom(5));
        
        footer.add(new Paragraph("Ganpati Bappa Morya!")
            .setFont(boldFont)
            .setFontSize(18)
            .setFontColor(primaryColor));
        
        document.add(footer);
        
        document.close();
        return baos;
    }

    private static void addTableRow(Table table, String label, String value, PdfFont labelFont, PdfFont valueFont) {
        Paragraph labelPara = new Paragraph(label)
            .setFont(labelFont)
            .setFontSize(14)
            .setFontColor(ColorConstants.DARK_GRAY);
        
        Paragraph valuePara = new Paragraph(value)
            .setFont(valueFont)
            .setFontSize(14);
        
        Cell labelCell = new Cell().add(labelPara);
        labelCell.setPadding(5);
        labelCell.setBorder(Border.NO_BORDER);
        
        Cell valueCell = new Cell().add(valuePara);
        valueCell.setPadding(5);
        valueCell.setBorder(Border.NO_BORDER);
        valueCell.setTextAlignment(TextAlignment.RIGHT);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}