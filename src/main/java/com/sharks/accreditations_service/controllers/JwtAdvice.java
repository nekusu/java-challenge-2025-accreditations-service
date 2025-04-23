package com.sharks.accreditations_service.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.sharks.accreditations_service.config.JwtUtils;
import com.sharks.accreditations_service.constants.ServiceURLs;
import com.sharks.accreditations_service.exceptions.RestTemplateException;
import com.sharks.accreditations_service.models.dtos.UserDTO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class JwtAdvice {

    private RestTemplate restTemplate;

    private JwtUtils jwtUtils;

    public JwtAdvice(RestTemplate restTemplate, JwtUtils jwtUtils) {
        this.restTemplate = restTemplate;
        this.jwtUtils = jwtUtils;
    }

    @ModelAttribute("user")
    public UserDTO fetchUser(HttpServletRequest request) {
        String token = jwtUtils.extractTokenFromRequest(request);
        if (token == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is missing or invalid");
        if (jwtUtils.isTokenExpired(token))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Expired JWT");

        String url = ServiceURLs.USERS_URL + "/self";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<UserDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, UserDTO.class);
            return response.getBody();
        } catch (ResourceAccessException | IllegalArgumentException e) {
            throw new RestTemplateException(HttpStatus.SERVICE_UNAVAILABLE, ServiceURLs.USERS_ENDPOINT, e.getMessage());
        }
    }
}
