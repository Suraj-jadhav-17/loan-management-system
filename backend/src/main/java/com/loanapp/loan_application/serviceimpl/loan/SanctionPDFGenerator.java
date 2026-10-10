package com.loanapp.loan_application.serviceimpl.loan;

import com.loanapp.loan_application.entity.loan.LoanType;
import com.loanapp.loan_application.entity.loan.SanctionLetter;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SanctionPDFGenerator {

        public byte[] generatePdf (SanctionLetter letter, String name, String phone, LoanType loanType) throws IOException {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
                Document document = new Document(PageSize.A4, 40, 40, 40, 40);
                PdfWriter writer = PdfWriter.getInstance(document, outputStream);
                document.open();

                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD,18 , Font.BOLD, Color.black);
                Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD,11, Font.BOLD, Color.black);
                Font normalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD,10 ,Font.NORMAL,Color.black);

                Paragraph title = new Paragraph("LOAN SANCTION LETTER", titleFont);
                title.setAlignment(Paragraph.ALIGN_CENTER);
                title.setSpacingAfter(25);
                document.add(title);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String sanctionDate = letter.getCreatedAt() != null
                        ? letter.getCreatedAt().format(formatter)
                        : "";

                Paragraph date = new Paragraph("Date: "+sanctionDate, normalFont);
                date.setSpacingAfter(20);
                document.add(date);

                document.add(new Paragraph("Dear " + name + ",", normalFont));

                Paragraph introduction = new Paragraph(
                        "Thank you for choosing ABC Bank. Based on your application "
                                + "and the information provided, we are pleased to extend "
                                + "an offer for a loan subject to the terms and conditions "
                                + "mentioned below.",
                        normalFont);

                introduction.setSpacingBefore(12);
                introduction.setSpacingAfter(20);
                document.add(introduction);

                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{1.15f,1.15f});
                addRow(table, "Application No", letter.getApplicationNo(), headingFont, normalFont);
                addRow(table,"Sanction Date", sanctionDate, headingFont, normalFont);
                addRow(table,"Mobile Number", phone, headingFont, normalFont);
                addRow(table ,"Loan Type", loanType.toString(), headingFont, normalFont);
                addRow(table, "Loan Amount Sanctioned", "Rs. " + letter.getLoanAmount(), headingFont, normalFont);
                addRow(table, "Reference Interest Rate", letter.getInterestRate() + "% per annum", headingFont, normalFont);
                addRow(table, "Loan Tenure", letter.getTenureMonth() + " Months", headingFont, normalFont);
                addRow(table, "Total Processing Charges", "Rs. 5,000.00", headingFont, normalFont);
                addRow(table, "Origination Fee", "Rs. 2,000.00", headingFont, normalFont);
                addRow(table, "Sanction Letter Validity", "180 Days", headingFont, normalFont);
                addRow(table, "Amount of EMI", "Rs. " + letter.getEmiAmount(), headingFont, normalFont);


                document.add(table);

                Paragraph conditionsHeading = new Paragraph(
                        "Additional Conditions To Comply Prior To Loan Disbursal:",
                        headingFont);

                conditionsHeading.setSpacingBefore(20);
                conditionsHeading.setSpacingAfter(10);
                document.add(conditionsHeading);

                List<String> conditions = List.of(
                        "Repayment from ABC Bank",
                        "Legal vetting and search to be conducted",
                        "NOC and offered collateral",
                        "Confirmation form, official ID and copy of ID"
                );

                for (int i = 0; i < conditions.size(); i++) {
                    Paragraph condition = new Paragraph(
                            (i + 1) + ". " + conditions.get(i), normalFont);

                    condition.setSpacingAfter(10);
                    document.add(condition);
                }
                Paragraph signatureHeading = new Paragraph("Authorized Signatory", headingFont);

                signatureHeading.setSpacingBefore(15);
                document.add(signatureHeading);

                document.add(new Paragraph("ABC Bank", normalFont));

                document.close();

                return outputStream.toByteArray();

            }catch (Exception e) {
                throw new RuntimeException("Failed to generate sanction letter PDF", e);
            }



    }
    private void addRow(PdfPTable table, String label, String value, Font headingFont, Font normalFont) {

        PdfPCell labelCell = new PdfPCell(
                new Phrase(label, headingFont));

        PdfPCell valueCell = new PdfPCell(
                new Phrase(value != null ? value : "", normalFont));

        labelCell.setPadding(6);
        valueCell.setPadding(6);

        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        labelCell.setBackgroundColor(new Color(245, 245, 245));

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}

