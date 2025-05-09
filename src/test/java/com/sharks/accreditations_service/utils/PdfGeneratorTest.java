package com.sharks.accreditations_service.utils;

import com.sharks.accreditations_service.models.dtos.AccreditationDTO;
import com.sharks.accreditations_service.models.dtos.UserDTO;
import com.sharks.accreditations_service.exceptions.PdfGenerationException;
import org.junit.jupiter.api.Test;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Field;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PdfGeneratorTest {

    private PdfGenerator pdfGenerator;

    @BeforeEach
    void setUp() {
        pdfGenerator = new PdfGenerator();
    }

    private UserDTO createUserDTO() {
        return new UserDTO(1L, "testuser", "test@example.com", "USER");
    }

    private AccreditationDTO createAccreditationDTO() {
        AccreditationDTO dto = new AccreditationDTO();
        // Using reflection to set private fields for testing
        try {
            Field id = AccreditationDTO.class.getDeclaredField("id");
            Field userId = AccreditationDTO.class.getDeclaredField("userId");
            Field salePointId = AccreditationDTO.class.getDeclaredField("salePointId");
            Field salePointName = AccreditationDTO.class.getDeclaredField("salePointName");
            Field amount = AccreditationDTO.class.getDeclaredField("amount");
            Field receiptDate = AccreditationDTO.class.getDeclaredField("receiptDate");
            id.setAccessible(true);
            userId.setAccessible(true);
            salePointId.setAccessible(true);
            salePointName.setAccessible(true);
            amount.setAccessible(true);
            receiptDate.setAccessible(true);

            id.set(dto, 1L);
            userId.set(dto, 1L);
            salePointId.set(dto, 7L);
            salePointName.set(dto, "Salta");
            amount.set(dto, 150.00);
            receiptDate.set(dto, LocalDate.of(2024, 6, 1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dto;
    }

    @Test
    void generateAccreditationPdf_ReturnsNonEmptyPdf() {
        UserDTO user = createUserDTO();
        AccreditationDTO accreditation = createAccreditationDTO();

        byte[] pdfBytes = pdfGenerator.generateAccreditationPdf(user, accreditation);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);

        // Check PDF header
        byte[] header = new byte[4];
        System.arraycopy(pdfBytes, 0, header, 0, 4);
        assertEquals("%PDF".getBytes()[0], header[0]);
    }

    @Test
    void generateAccreditationPdf_ThrowsExceptionOnNullInput() {
        UserDTO user = createUserDTO();
        AccreditationDTO accreditation = createAccreditationDTO();

        assertThrows(PdfGenerationException.class, () -> {
            pdfGenerator.generateAccreditationPdf(null, accreditation);
        });

        assertThrows(PdfGenerationException.class, () -> {
            pdfGenerator.generateAccreditationPdf(user, null);
        });
    }

    @Test
    void generateAccreditationPdf_ContainsExpectedContent() {
        UserDTO user = createUserDTO();
        AccreditationDTO accreditation = createAccreditationDTO();

        byte[] pdfBytes = pdfGenerator.generateAccreditationPdf(user, accreditation);

        // Extract text from PDF bytes using PDFBox
        String pdfContent = "";
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            pdfContent = stripper.getText(document);
        } catch (Exception e) {
            fail("Failed to extract text from PDF: " + e.getMessage());
        }

        assertTrue(pdfContent.contains("Accreditation Receipt"));
        assertTrue(pdfContent.contains("test@example.com"));
        assertTrue(pdfContent.contains("Salta"));
        assertTrue(pdfContent.contains("$150"));
    }
}
