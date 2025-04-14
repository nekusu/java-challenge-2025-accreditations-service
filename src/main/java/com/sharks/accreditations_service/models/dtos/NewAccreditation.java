package com.sharks.accreditations_service.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record NewAccreditation(@NotNull @Positive Long salePointId, @NotNull @PositiveOrZero Double amount) {
}
