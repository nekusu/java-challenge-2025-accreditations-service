package com.sharks.accreditations_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharks.accreditations_service.models.Accreditation;

public interface AccreditationRepository extends JpaRepository<Accreditation, Long> {
}
