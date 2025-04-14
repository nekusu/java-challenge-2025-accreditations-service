package com.sharks.accreditations_service.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class RestTemplateException extends ResponseStatusException {
    private final String path;

    public RestTemplateException(HttpStatusCode status, String path, String message) {
        super(status, message);
        this.path = path;
    }
}
