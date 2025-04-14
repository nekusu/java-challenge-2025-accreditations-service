package com.sharks.accreditations_service.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

@Component
public class RestTemplateErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean hasError(@NonNull ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(@NonNull ClientHttpResponse response) throws IOException {
        String errorBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        JsonNode errorAttributes = objectMapper.readTree(errorBody);
        throw new ResponseStatusException(response.getStatusCode(), errorAttributes.get("message").asText());
    }
}
