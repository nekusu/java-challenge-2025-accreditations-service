package com.sharks.accreditations_service.services;

import com.sharks.accreditations_service.exceptions.AccreditationAccessDeniedException;
import com.sharks.accreditations_service.exceptions.AccreditationNotFoundException;
import com.sharks.accreditations_service.exceptions.RestTemplateException;
import com.sharks.accreditations_service.models.Accreditation;
import com.sharks.accreditations_service.models.dtos.AccreditationDTO;
import com.sharks.accreditations_service.models.dtos.NewAccreditation;
import com.sharks.accreditations_service.models.dtos.SalePointDTO;
import com.sharks.accreditations_service.models.dtos.UserDTO;
import com.sharks.accreditations_service.repositories.AccreditationRepository;
import com.sharks.accreditations_service.services.impl.AccreditationServiceImpl;
import com.sharks.accreditations_service.utils.PdfGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccreditationServiceTest {

    @Mock
    private AccreditationRepository accreditationRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AmqpTemplate amqpTemplate;

    @Mock
    private PdfGenerator pdfGenerator;

    @InjectMocks
    private AccreditationServiceImpl accreditationService;

    private Accreditation accreditation;
    private NewAccreditation newAccreditation;
    private UserDTO userDTO;
    private SalePointDTO salePointDTO;

    @BeforeEach
    void setUp() {
        accreditation = new Accreditation(
                1L,
                1L,
                "Test Sale Point",
                100.0,
                LocalDate.now());
        ReflectionTestUtils.setField(accreditation, "id", 1L);
        newAccreditation = new NewAccreditation(1L, 100.0);
        userDTO = new UserDTO(1L, "Test User", "test@email.com", "USER");
        salePointDTO = new SalePointDTO();
        ReflectionTestUtils.setField(salePointDTO, "id", 1L);
        ReflectionTestUtils.setField(salePointDTO, "name", "Test Sale Point");
    }

    @Test
    void testGetAllAccreditationDTOs_ReturnsList() {
        when(accreditationRepository.findAll()).thenReturn(List.of(accreditation));

        List<AccreditationDTO> result = accreditationService.getAllAccreditationDTOs();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Sale Point", result.get(0).getSalePointName());
    }

    @Test
    void testGetAllAccreditationDTOs_ReturnsEmptyList() {
        when(accreditationRepository.findAll()).thenReturn(Collections.emptyList());

        List<AccreditationDTO> result = accreditationService.getAllAccreditationDTOs();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAccreditationById_Found() {
        when(accreditationRepository.findById(1L)).thenReturn(Optional.of(accreditation));

        Accreditation result = accreditationService.getAccreditationById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetAccreditationById_NotFound_ThrowsException() {
        when(accreditationRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(AccreditationNotFoundException.class, () -> accreditationService.getAccreditationById(2L));
    }

    @Test
    void testGetAccreditationsByUserId_ReturnsList() {
        when(accreditationRepository.findByUserId(1L)).thenReturn(List.of(accreditation));

        List<Accreditation> result = accreditationService.getAccreditationsByUserId(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
    }

    @Test
    void testGetAccreditationsByUserId_ReturnsEmptyList() {
        when(accreditationRepository.findByUserId(2L)).thenReturn(Collections.emptyList());

        List<Accreditation> result = accreditationService.getAccreditationsByUserId(2L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testVerifyAccreditationOwnership_Success() {
        when(accreditationRepository.findById(1L)).thenReturn(Optional.of(accreditation));

        assertDoesNotThrow(() -> accreditationService.verifyAccreditationOwnership(1L, 1L));
    }

    @Test
    void testVerifyAccreditationOwnership_AccessDenied_ThrowsException() {
        when(accreditationRepository.findById(1L)).thenReturn(Optional.of(accreditation));

        assertThrows(AccreditationAccessDeniedException.class,
                () -> accreditationService.verifyAccreditationOwnership(1L, 99L));
    }

    @Test
    void testCreateAccreditation_Success() {
        when(restTemplate.getForObject(anyString(), eq(SalePointDTO.class))).thenReturn(salePointDTO);
        when(accreditationRepository.save(any(Accreditation.class))).thenReturn(accreditation);

        AccreditationDTO result = accreditationService.createAccreditation(newAccreditation, userDTO);
        assertNotNull(result);
        assertEquals("Test Sale Point", result.getSalePointName());
        assertEquals(100.0, result.getAmount());
    }

    @Test
    void testCreateAccreditation_ServiceUnavailable_ThrowsException() {
        when(restTemplate.getForObject(anyString(), eq(SalePointDTO.class)))
                .thenThrow(new ResourceAccessException("Service unavailable"));

        assertThrows(RestTemplateException.class,
                () -> accreditationService.createAccreditation(newAccreditation, userDTO));
    }
}
