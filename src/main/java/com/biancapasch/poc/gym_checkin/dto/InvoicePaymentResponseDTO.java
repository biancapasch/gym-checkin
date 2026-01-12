package com.biancapasch.poc.gym_checkin.dto;

import com.biancapasch.poc.gym_checkin.domain.entity.InvoiceStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record InvoicePaymentResponseDTO(
        Long invoiceId,
        Long customerId,
        BigDecimal amount,
        OffsetDateTime paidAt,
        InvoiceStatus status
) {
}
