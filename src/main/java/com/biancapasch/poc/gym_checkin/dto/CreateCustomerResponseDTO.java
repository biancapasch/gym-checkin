package com.biancapasch.poc.gym_checkin.dto;

public record CreateCustomerResponseDTO(
        String firstName,
        String lastName,
        String email
) {
}
