package com.sharks.accreditations_service.services;

import java.util.List;

import com.sharks.accreditations_service.models.Accreditation;
import com.sharks.accreditations_service.models.dtos.AccreditationDTO;
import com.sharks.accreditations_service.models.dtos.NewAccreditation;

public interface AccreditationService {

    List<AccreditationDTO> getAllAccreditations();

    Accreditation getAccreditationById(Long id);

    AccreditationDTO getAccreditationDTOById(Long id);

    AccreditationDTO createAccreditation(NewAccreditation newAccreditation);
}
