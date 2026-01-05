package com.biancapasch.poc.gym_checkin.controller;

import com.biancapasch.poc.gym_checkin.dto.CreateCheckinRequestDTO;
import com.biancapasch.poc.gym_checkin.dto.CreateCheckinResponseDTO;
import com.biancapasch.poc.gym_checkin.service.CheckinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers/{customerId}/checkins")
public class CheckinController {

    private final CheckinService service;

    @PostMapping
    public ResponseEntity<CreateCheckinResponseDTO> create(
            @PathVariable Long customerId,
            @RequestBody @Valid CreateCheckinRequestDTO request
    ) {
        CreateCheckinResponseDTO checkin =
                service.createCheckin(customerId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(checkin);
    }

    @GetMapping
    public ResponseEntity<Page<CreateCheckinResponseDTO>> list(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CreateCheckinResponseDTO> dtoPage =
                service.findAllCheckins(customerId, page, size);

        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/open")
    public ResponseEntity<CreateCheckinResponseDTO> getOpenSession(@PathVariable Long customerId) {
        Optional<CreateCheckinResponseDTO> open = service.findOpenSession(customerId);

        return open
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
