package com.sharks.accreditations_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    private final RestTemplateErrorHandler restTemplateErrorHandler;

    public RestTemplateConfig(RestTemplateErrorHandler restTemplateErrorHandler) {
        this.restTemplateErrorHandler = restTemplateErrorHandler;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(restTemplateErrorHandler);
        return restTemplate;
    }
}
