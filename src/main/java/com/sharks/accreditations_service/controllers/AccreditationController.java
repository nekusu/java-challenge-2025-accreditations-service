package com.sharks.accreditations_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharks.accreditations_service.models.dtos.AccreditationDTO;
import com.sharks.accreditations_service.models.dtos.NewAccreditation;
import com.sharks.accreditations_service.services.AccreditationService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/accreditations")
public class AccreditationController {

    private final AccreditationService accreditationService;

    public AccreditationController(AccreditationService accreditationService) {
        this.accreditationService = accreditationService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AccreditationDTO> getAllAccreditations() {
        return accreditationService.getAllAccreditations();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccreditationDTO getAccreditationById(@PathVariable Long id) {
        return accreditationService.getAccreditationDTOById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccreditationDTO createAccreditation(@RequestBody @Valid NewAccreditation newAccreditation) {
        return accreditationService.createAccreditation(newAccreditation);
    }
}
