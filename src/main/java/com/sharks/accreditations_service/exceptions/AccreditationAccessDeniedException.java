package com.sharks.accreditations_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccreditationAccessDeniedException extends RuntimeException {

    public AccreditationAccessDeniedException() {
        super("Access to this accreditation not allowed");
    }

    public AccreditationAccessDeniedException(String message) {
        super(message);
    }
}
