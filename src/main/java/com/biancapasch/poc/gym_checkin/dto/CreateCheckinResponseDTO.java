package com.biancapasch.poc.gym_checkin.dto;

import java.time.OffsetDateTime;

public record CreateCheckinResponseDTO(
        Long checkinId,
        Long customerId,
        OffsetDateTime checkinTime,
        OffsetDateTime checkoutTime
) {}
