package com.sharks.accreditations_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccreditationNotFoundException extends RuntimeException {

    public AccreditationNotFoundException(Long id) {
        super("Accreditation not found with id: " + id);
    }

    public AccreditationNotFoundException(String message) {
        super(message);
    }
}
