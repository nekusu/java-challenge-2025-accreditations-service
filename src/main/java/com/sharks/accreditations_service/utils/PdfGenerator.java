package com.sharks.accreditations_service.utils;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.sharks.accreditations_service.exceptions.PdfGenerationException;
import com.sharks.accreditations_service.models.dtos.AccreditationDTO;
import com.sharks.accreditations_service.models.dtos.UserDTO;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PdfGenerator {

    public byte[] generateAccreditationPdf(UserDTO user, AccreditationDTO accreditation) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Accreditation Receipt", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(80);
            table.setWidths(new float[] { 1f, 2f });
            table.setSpacingBefore(10);

            addCell(table, "Receipt Date:", Element.ALIGN_LEFT, true);
            addCell(table, dtf.format(accreditation.getReceiptDate()), Element.ALIGN_LEFT, false);

            addCell(table, "Accreditation ID:", Element.ALIGN_LEFT, true);
            addCell(table, String.valueOf(accreditation.getId()), Element.ALIGN_LEFT, false);

            addCell(table, "User Email:", Element.ALIGN_LEFT, true);
            addCell(table, String.valueOf(user.getEmail()), Element.ALIGN_LEFT, false);

            addCell(table, "Sale Point ID:", Element.ALIGN_LEFT, true);
            addCell(table, String.valueOf(accreditation.getSalePointId()), Element.ALIGN_LEFT, false);

            addCell(table, "Sale Point Name:", Element.ALIGN_LEFT, true);
            addCell(table, accreditation.getSalePointName(), Element.ALIGN_LEFT, false);

            addCell(table, "Amount:", Element.ALIGN_LEFT, true);
            addCell(table, String.format("$%.2f", accreditation.getAmount()), Element.ALIGN_RIGHT, false);

            document.add(table);

            document.close();
        } catch (Exception e) {
            log.error("Error generating Accreditation PDF: {}", e.getMessage());
            throw new PdfGenerationException("Could not generate accreditation PDF");
        }

        return outputStream.toByteArray();
    }

    private void addCell(PdfPTable table, String text, int align, boolean header) {
        Font font = header
                ? new Font(Font.HELVETICA, 12, Font.BOLD)
                : new Font(Font.HELVETICA, 12, Font.NORMAL);

        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        cell.setBorderColor(Color.LIGHT_GRAY);
        cell.setPadding(5);
        if (header) {
            cell.setBackgroundColor(new Color(230, 230, 230));
        }
        table.addCell(cell);
    }
}
