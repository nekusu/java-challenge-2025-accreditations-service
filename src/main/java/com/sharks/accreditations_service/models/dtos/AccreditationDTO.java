package com.sharks.accreditations_service.models.dtos;

import java.time.LocalDate;

import com.sharks.accreditations_service.models.Accreditation;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccreditationDTO {

    private Long id;
    private Long salePointId;
    private String salePointName;
    private Double amount;
    private LocalDate receiptDate;

    public AccreditationDTO(Accreditation accreditation) {
        this.id = accreditation.getId();
        this.salePointId = accreditation.getSalePointId();
        this.salePointName = accreditation.getSalePointName();
        this.amount = accreditation.getAmount();
        this.receiptDate = accreditation.getReceiptDate();
    }
}
