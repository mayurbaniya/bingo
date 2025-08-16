package com.hp.bingo.service.mail;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Line;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.ByteArrayOutputStream;

public class GanpatiFestivalPassGenerator {

    // Vibrant festival color palette
    private static final DeviceRgb DEEP_SAFFRON = new DeviceRgb(255, 140, 0);
    private static final DeviceRgb RICH_RED = new DeviceRgb(200, 40, 30);
    private static final DeviceRgb GOLD = new DeviceRgb(255, 215, 0);
    private static final DeviceRgb CREAM = new DeviceRgb(255, 248, 225);
    private static final DeviceRgb DARK_GREEN = new DeviceRgb(30, 90, 50);

    public static void main(String[] args) {
        System.out.println("Generating Ganpati Festival Pass...");

        // Create sample data
        EntryForm form = new EntryForm();
        form.setName("Amit Sharma");
        form.setTickets(4);
        form.setPhone("9876543210");
        form.setEmail("amit.sharma@example.com");
        form.setRegistrationId("GANPATI-2025-12345");

        try {
            ByteArrayOutputStream baos = generateFestivalPass(form);

            // Save to file for testing
            java.nio.file.Files.write(
                    java.nio.file.Paths.get("Ganpati_Premium_Pass.pdf"),
                    baos.toByteArray()
            );

            System.out.println("Successfully generated: Ganpati_Premium_Pass.pdf");
        } catch (Exception e) {
            System.err.println("Error generating pass:");
            e.printStackTrace();
        }
    }

    public static ByteArrayOutputStream generateFestivalPass(EntryForm entryForm) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);

        // Perfect ticket size for single page
        PageSize customSize = new PageSize(400, 600);
        Document document = new Document(pdf, customSize);
        document.setMargins(30, 30, 30, 30);

        // Load fonts
        PdfFont titleFont = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
        PdfFont headerFont = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
        PdfFont regularFont = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

        // Background with decorative border
        Div background = new Div()
                .setBackgroundColor(CREAM)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(DEEP_SAFFRON, 4))
                .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(20))
                .setHeight(customSize.getHeight() - 60)
                .setWidth(customSize.getWidth() - 60)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        document.add(background);

        // Decorative top elements
        Div headerDecoration = new Div()
                .setBackgroundColor(DEEP_SAFFRON)
                .setBackgroundColor(DEEP_SAFFRON)
                .setHeight(80)
                .setWidth(UnitValue.createPercentValue(100))
                .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(15))
                .setMarginBottom(20);

        // Festival title with decorative underline
        Div titleContainer = new Div()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(25);

        Paragraph mainTitle = new Paragraph("GANAPATI FESTIVAL 2025")
                .setFont(titleFont)
                .setFontSize(26)
                .setFontColor(ColorConstants.WHITE)
                .setMarginBottom(5);

        Paragraph subTitle = new Paragraph("PREMIUM ENTRY PASS")
                .setFont(regularFont)
                .setFontSize(14)
                .setFontColor(GOLD)
                .setMarginBottom(10);

        // Decorative underline
        // Decorative underline using LineSeparator
        // Line separator = new Line();
        // separator.setStrokeColor(GOLD);
        // separator.setLineWidth(2);
        // separator.setWidth(150);
        // separator.setHorizontalAlignment(HorizontalAlignment.CENTER);

        titleContainer.add(mainTitle);
        titleContainer.add(subTitle);
        // titleContainer.add(separator);
        document.add(titleContainer);
        // Ticket holder section
        Div ticketHolder = new Div()
                .setBackgroundColor(new DeviceRgb(255, 255, 255))
                .setOpacity(0.8f)
                .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(15))
                .setPadding(20)
                .setMarginBottom(25)
                .setTextAlignment(TextAlignment.CENTER);

        ticketHolder.add(new Paragraph("HOLDER")
                .setFont(regularFont)
                .setFontSize(12)
                .setFontColor(DARK_GREEN)
                .setMarginBottom(5));

        ticketHolder.add(new Paragraph(entryForm.getName().toUpperCase())
                .setFont(headerFont)
                .setFontSize(28)
                .setFontColor(RICH_RED)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10));

        // Decorative flourish
        ticketHolder.add(new Paragraph("✻✻✻")
                .setFont(regularFont)
                .setFontSize(20)
                .setFontColor(DEEP_SAFFRON));
        document.add(ticketHolder);

        // Ticket details in elegant table
        float[] columnWidths = {40, 60};
        Table detailsTable = new Table(columnWidths)
                .setWidth(UnitValue.createPercentValue(90))
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setMarginBottom(30);

        addDetailRow(detailsTable, "TICKETS:", String.valueOf(entryForm.getTickets()), regularFont);
        addDetailRow(detailsTable, "CONTACT:", entryForm.getPhone(), regularFont);
        addDetailRow(detailsTable, "EMAIL:", entryForm.getEmail(), regularFont);
        addDetailRow(detailsTable, "REGISTRATION ID:", entryForm.getRegistrationId(), regularFont);
        addDetailRow(detailsTable, "ENTRY TYPE:", "PREMIUM PASS", regularFont);

        document.add(detailsTable);

        // Event details section
        Div eventContainer = new Div()
                .setBackgroundColor(GOLD)
                .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(15))
                .setPadding(20)
                .setMarginBottom(25);

        eventContainer.add(new Paragraph("EVENT DETAILS")
                .setFont(headerFont)
                .setFontSize(18)
                .setFontColor(DARK_GREEN)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(15));

        // Event details with icons
        addEventDetail(eventContainer, "📅", "DATE & TIME", "15th September 2025 | 7:00 PM Onwards");
        addEventDetail(eventContainer, "🏛️", "VENUE", "Ganapati Hall, Main Street, Mumbai");
        addEventDetail(eventContainer, "🎫", "ADMISSION", "Present this pass at entrance");

        document.add(eventContainer);

        // Traditional footer
        Div footer = new Div()
                .setTextAlignment(TextAlignment.CENTER);

        footer.add(new Paragraph("Ganpati Bappa Morya!")
                .setFont(headerFont)
                .setFontSize(24)
                .setFontColor(DEEP_SAFFRON)
                .setMarginBottom(5));

        footer.add(new Paragraph("www.ganapatifestival2025.in")
                .setFont(regularFont)
                .setFontSize(10)
                .setFontColor(DARK_GREEN));

        document.add(footer);

        document.close();
        return baos;
    }

    private static void addDetailRow(Table table, String label, String value, PdfFont font) {
        Cell labelCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(8)
                .add(new Paragraph(label)
                        .setFont(font)
                        .setFontSize(14)
                        .setFontColor(DARK_GREEN)
                        .setBold());

        Cell valueCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setPadding(8)
                .add(new Paragraph(value)
                        .setFont(font)
                        .setFontSize(14)
                        .setFontColor(ColorConstants.BLACK));

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private static void addEventDetail(Div container, String icon, String label, String value) {
        Paragraph p = new Paragraph()
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(10);

        p.add(new Text(icon + "  ")
                .setFontSize(18));

        p.add(new Text(label + ": ")
                .setBold()
                .setFontSize(12)
                .setFontColor(DARK_GREEN));

        p.add(new Text(value)
                .setFontSize(12)
                .setFontColor(ColorConstants.BLACK));

        container.add(p);
    }

    // Simple EntryForm class for testing
    public static class EntryForm {
        private String name;
        private int tickets;
        private String phone;
        private String email;
        private String registrationId;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getTickets() { return tickets; }
        public void setTickets(int tickets) { this.tickets = tickets; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRegistrationId() { return registrationId; }
        public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }
    }
}