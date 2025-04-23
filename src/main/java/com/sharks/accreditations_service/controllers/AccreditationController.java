package com.sharks.accreditations_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharks.accreditations_service.exceptions.AccreditationAccessDeniedException;
import com.sharks.accreditations_service.models.dtos.AccreditationDTO;
import com.sharks.accreditations_service.models.dtos.NewAccreditation;
import com.sharks.accreditations_service.models.dtos.UserDTO;
import com.sharks.accreditations_service.services.AccreditationService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public List<AccreditationDTO> getAccreditations(@ModelAttribute("user") UserDTO user) {
        if (user.getRole().equals("ADMIN"))
            return accreditationService.getAllAccreditationDTOs();
        return accreditationService.getAccreditationDTOsByUserId(user.getId());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccreditationDTO getAccreditationById(@PathVariable Long id, @ModelAttribute("user") UserDTO user) {
        if (!user.getRole().equals("ADMIN"))
            accreditationService.verifyAccreditationOwnership(id, user.getId());
        return accreditationService.getAccreditationDTOById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccreditationDTO createAccreditation(@RequestBody @Valid NewAccreditation newAccreditation,
            @ModelAttribute("user") UserDTO user) {
        if (user.getRole().equals("ADMIN"))
            throw new AccreditationAccessDeniedException("Admins are not allowed to create orders");
        return accreditationService.createAccreditation(newAccreditation, user.getId());
    }
}
