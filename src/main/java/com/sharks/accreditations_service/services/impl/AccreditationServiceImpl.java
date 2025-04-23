package com.sharks.accreditations_service.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.sharks.accreditations_service.constants.ServiceURLs;
import com.sharks.accreditations_service.exceptions.AccreditationAccessDeniedException;
import com.sharks.accreditations_service.exceptions.AccreditationNotFoundException;
import com.sharks.accreditations_service.exceptions.RestTemplateException;
import com.sharks.accreditations_service.models.Accreditation;
import com.sharks.accreditations_service.models.dtos.AccreditationDTO;
import com.sharks.accreditations_service.models.dtos.NewAccreditation;
import com.sharks.accreditations_service.models.dtos.SalePointDTO;
import com.sharks.accreditations_service.repositories.AccreditationRepository;
import com.sharks.accreditations_service.services.AccreditationService;

@Service
public class AccreditationServiceImpl implements AccreditationService {

    private final RestTemplate restTemplate;
    private final AccreditationRepository accreditationRepository;

    public AccreditationServiceImpl(RestTemplate restTemplate, AccreditationRepository accreditationRepository) {
        this.restTemplate = restTemplate;
        this.accreditationRepository = accreditationRepository;
    }

    @Override
    public List<AccreditationDTO> getAllAccreditationDTOs() {
        return accreditationRepository.findAll().stream().map(AccreditationDTO::new).toList();
    }

    @Override
    public Accreditation getAccreditationById(Long id) {
        return accreditationRepository.findById(id).orElseThrow(() -> new AccreditationNotFoundException(id));
    }

    @Override
    public AccreditationDTO getAccreditationDTOById(Long id) {
        return new AccreditationDTO(getAccreditationById(id));
    }

    @Override
    public List<Accreditation> getAccreditationsByUserId(Long userId) {
        return accreditationRepository.findByUserId(userId);
    }

    @Override
    public List<AccreditationDTO> getAccreditationDTOsByUserId(Long userId) {
        return getAccreditationsByUserId(userId).stream().map(AccreditationDTO::new).toList();
    }

    @Override
    public void verifyAccreditationOwnership(Long id, Long userId) {
        Accreditation order = getAccreditationById(id);
        if (!userId.equals(order.getUserId()))
            throw new AccreditationAccessDeniedException();
    }

    @Override
    public AccreditationDTO createAccreditation(NewAccreditation newAccreditation, Long userId) {
        SalePointDTO salePointDTO = fetchSalePoint(newAccreditation.salePointId());
        Accreditation accreditation = new Accreditation(
                userId,
                salePointDTO.getId(),
                salePointDTO.getName(),
                newAccreditation.amount(),
                LocalDate.now());
        Accreditation savedAccreditation = accreditationRepository.save(accreditation);
        return new AccreditationDTO(savedAccreditation);
    }

    private SalePointDTO fetchSalePoint(Long id) {
        String url = ServiceURLs.SALE_POINTS_URL + "/" + id;
        try {
            return restTemplate.getForObject(url, SalePointDTO.class);
        } catch (ResourceAccessException | IllegalArgumentException e) {
            throw new RestTemplateException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    ServiceURLs.SALE_POINTS_ENDPOINT,
                    e.getMessage());
        }
    }
}
