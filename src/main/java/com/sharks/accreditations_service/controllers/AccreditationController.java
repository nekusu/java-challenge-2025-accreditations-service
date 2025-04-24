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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Accreditations", description = "Operations related to accreditations management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/accreditations")
public class AccreditationController {

    private final AccreditationService accreditationService;

    public AccreditationController(AccreditationService accreditationService) {
        this.accreditationService = accreditationService;
    }

    @Operation(summary = "Get accreditations", description = "Returns a list of accreditations. Admins receive all accreditations, while regular users receive only their own.", responses = {
            @ApiResponse(responseCode = "200", description = "List of accreditations retrieved successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AccreditationDTO.class))))
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AccreditationDTO> getAccreditations(@Parameter(hidden = true) @ModelAttribute("user") UserDTO user) {
        if (user.getRole().equals("ADMIN"))
            return accreditationService.getAllAccreditationDTOs();
        return accreditationService.getAccreditationDTOsByUserId(user.getId());
    }

    @Operation(summary = "Get accreditation by ID", description = "Returns an accreditation by its ID. Admins can access any accreditation. Regular users can only access their own accreditations.", parameters = {
            @Parameter(name = "id", description = "ID of the accreditation", required = true, example = "1")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "Accreditation retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccreditationDTO.class))),
            @ApiResponse(responseCode = "404", description = "Accreditation not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccreditationDTO getAccreditationById(@PathVariable Long id,
            @Parameter(hidden = true) @ModelAttribute("user") UserDTO user) {
        if (!user.getRole().equals("ADMIN"))
            accreditationService.verifyAccreditationOwnership(id, user.getId());
        return accreditationService.getAccreditationDTOById(id);
    }

    @Operation(summary = "Create a new accreditation", description = "Creates a new accreditation for the authenticated user. Admins are not allowed to create accreditations.", requestBody = @RequestBody(description = "Accreditation data to create", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = NewAccreditation.class))), responses = {
            @ApiResponse(responseCode = "201", description = "Accreditation created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccreditationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Admins are not allowed to create accreditations")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccreditationDTO createAccreditation(
            @org.springframework.web.bind.annotation.RequestBody @Valid NewAccreditation newAccreditation,
            @Parameter(hidden = true) @ModelAttribute("user") UserDTO user) {
        if (user.getRole().equals("ADMIN"))
            throw new AccreditationAccessDeniedException("Admins are not allowed to create orders");
        return accreditationService.createAccreditation(newAccreditation, user.getId());
    }
}
