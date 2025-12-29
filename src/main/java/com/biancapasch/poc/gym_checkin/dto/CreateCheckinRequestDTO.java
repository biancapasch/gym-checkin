package com.biancapasch.poc.gym_checkin.dto;

import com.biancapasch.poc.gym_checkin.CheckinType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateCheckinRequestDTO(
        @NotNull
        @Positive
        Long customerId,

        @NotNull
        CheckinType type
) {
}
