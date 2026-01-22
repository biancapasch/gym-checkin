package com.biancapasch.poc.gym_checkin.controller;

import com.biancapasch.poc.gym_checkin.dto.CreateCustomerRequestDTO;
import com.biancapasch.poc.gym_checkin.dto.CreateCustomerResponseDTO;
import com.biancapasch.poc.gym_checkin.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCustomerAndReturn201() throws Exception {
        LocalDate expectedPaymentDate = LocalDate.of(2026, 2, 10);

        CreateCustomerRequestDTO request = new CreateCustomerRequestDTO(
                "Bianca",
                "Paschoal",
                expectedPaymentDate,
                "bianca@example.com"
        );

        CreateCustomerResponseDTO response = new CreateCustomerResponseDTO(
                "Bianca",
                "Paschoal",
                "bianca@example.com"
        );

        when(customerService.create(any(CreateCustomerRequestDTO.class)))
                .thenReturn(response);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Bianca"))
                .andExpect(jsonPath("$.lastName").value("Paschoal"))
                .andExpect(jsonPath("$.email").value("bianca@example.com"));

        verify(customerService).create(any(CreateCustomerRequestDTO.class));
    }
}
