package com.biancapasch.poc.gym_checkin.controller;

import com.biancapasch.poc.gym_checkin.dto.CreateCheckinRequestDTO;
import com.biancapasch.poc.gym_checkin.dto.CreateCheckinResponseDTO;
import com.biancapasch.poc.gym_checkin.service.CheckinService;
import com.biancapasch.poc.gym_checkin.service.SessionCheckinStatus;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/checkin")
public class CheckinController {

    private final CheckinService service;

    @PostMapping
    public ResponseEntity<CreateCheckinResponseDTO> create(@RequestBody @Valid CreateCheckinRequestDTO response) {
        CreateCheckinResponseDTO checkin = service.createCheckin(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(checkin);
    }

    @GetMapping("/status/{checkinId}")
    public ResponseEntity<SessionCheckinStatus> getStatus(@PathVariable Long checkinId){
        SessionCheckinStatus status = service.getSessionStatus(checkinId);
        return ResponseEntity.ok(status);
    }

}
