package com.sharks.accreditations_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharks.accreditations_service.models.Accreditation;

public interface AccreditationRepository extends JpaRepository<Accreditation, Long> {

    List<Accreditation> findByUserId(Long userId);
}
