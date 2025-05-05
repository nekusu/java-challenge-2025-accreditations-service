package com.sharks.accreditations_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class PdfGenerationException extends RuntimeException {

    public PdfGenerationException() {
        super("Failed to generate PDF");
    }

    public PdfGenerationException(String message) {
        super(message);
    }
}
