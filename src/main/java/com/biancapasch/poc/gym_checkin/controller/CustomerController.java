package com.biancapasch.poc.gym_checkin.controller;

import com.biancapasch.poc.gym_checkin.dto.CreateCustomerRequestDTO;
import com.biancapasch.poc.gym_checkin.dto.CreateCustomerResponseDTO;
import com.biancapasch.poc.gym_checkin.service.CustomerCheckinStatus;
import com.biancapasch.poc.gym_checkin.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    public ResponseEntity<CreateCustomerResponseDTO> create(@RequestBody CreateCustomerRequestDTO request) {
        CreateCustomerResponseDTO response = service.create(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{customerId}")
    public ResponseEntity<CustomerCheckinStatus> getCustomerStatus(@PathVariable Long customerId) {
        CustomerCheckinStatus status = service.getCustomerStatus(customerId);
        return ResponseEntity.ok(status);
    }
}

