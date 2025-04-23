package com.sharks.accreditations_service.services;

import java.util.List;

import com.sharks.accreditations_service.models.Accreditation;
import com.sharks.accreditations_service.models.dtos.AccreditationDTO;
import com.sharks.accreditations_service.models.dtos.NewAccreditation;

public interface AccreditationService {

    List<AccreditationDTO> getAllAccreditationDTOs();

    Accreditation getAccreditationById(Long id);

    AccreditationDTO getAccreditationDTOById(Long id);

    List<Accreditation> getAccreditationsByUserId(Long userId);

    List<AccreditationDTO> getAccreditationDTOsByUserId(Long userId);

    void verifyAccreditationOwnership(Long id, Long userId);

    AccreditationDTO createAccreditation(NewAccreditation newAccreditation, Long userId);
}
