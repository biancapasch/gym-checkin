package com.biancapasch.poc.gym_checkin.controller;

import com.biancapasch.poc.gym_checkin.dto.CreateCustomerRequestDTO;
import com.biancapasch.poc.gym_checkin.dto.CreateCustomerResponseDTO;
import com.biancapasch.poc.gym_checkin.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    public ResponseEntity<CreateCustomerResponseDTO> create(@Valid @RequestBody CreateCustomerRequestDTO request) {
        CreateCustomerResponseDTO response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}

