package com.biancapasch.poc.gym_checkin.dto;

import com.biancapasch.poc.gym_checkin.CheckinType;
import jakarta.validation.constraints.NotNull;

public record CreateCheckinRequestDTO(
        @NotNull
        CheckinType type
) {}

