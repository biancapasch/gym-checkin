package com.biancapasch.poc.gym_checkin.controller;

import com.biancapasch.poc.gym_checkin.service.CheckinService;
import com.biancapasch.poc.gym_checkin.service.SessionCheckinStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/checkins")
public class CheckinStatusController {

    private final CheckinService service;

    @GetMapping("/{checkinId}/status")
    public ResponseEntity<SessionCheckinStatus> getStatus(
            @PathVariable Long checkinId
    ) {
        return ResponseEntity.ok(service.getSessionStatus(checkinId));
    }
}
