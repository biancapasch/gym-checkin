package com.biancapasch.poc.gym_checkin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateCustomerRequestDTO (
        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotNull
        LocalDate expectedPaymentDate,

        @NotBlank
        @Email
        String email
){
}
