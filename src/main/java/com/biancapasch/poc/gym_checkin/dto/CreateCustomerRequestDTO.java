package com.biancapasch.poc.gym_checkin.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequestDTO (
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotBlank
        String email
){
}
