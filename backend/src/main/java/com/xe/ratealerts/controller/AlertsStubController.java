package com.xe.ratealerts.controller;

import com.xe.ratealerts.model.dto.Alert;
import com.xe.ratealerts.model.dto.request.CreateAlertRequestDto;
import com.xe.ratealerts.model.dto.response.AlertResponseDto;
import com.xe.ratealerts.model.dto.response.ResponseDto;
import com.xe.ratealerts.service.AlertsStubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

// =====================================================================================
// STUB CONTROLLER - provided for the FRONTEND track.
//
// This gives frontend candidates a working /api/alerts API so they can build the alert
// management UI without writing backend code. It keeps alerts in an in-memory list and
// evaluates the "triggered" flag against the canned rates below, so creating an alert
// with a threshold on the wrong side of the canned rate will show as triggered.
//
// BACKEND-TRACK CANDIDATES: this is not a partial solution and you are not expected to
// keep it. Replace it or delete it; the alert feature is yours to design.
// =====================================================================================


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/alerts")
@Log4j2
public class AlertsStubController {
    private final AlertsStubService alertsStubService;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-User-ID") String systemId, @RequestBody CreateAlertRequestDto request) {
        log.info("Creating a new alert for user with id {}", systemId);
        ResponseDto response = alertsStubService.createAlert(systemId, request);
        if (!response.isSuccess()) {
            return ResponseEntity.status(response.getStatus()).body(response.getError());
        }
        return ResponseEntity.status(response.getStatus()).body(response.getSavedAlert());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestHeader("X-User-ID") String systemId, @PathVariable String id) {
        log.info("Deleting alert with id: {} for user with id {}", id, systemId);
        ResponseDto response = alertsStubService.deleteAlert(systemId, id);
        return ResponseEntity.status(response.getStatus()).build();
    }

    @GetMapping
    public ResponseEntity<Flux<AlertResponseDto>> getRates(@RequestHeader("X-User-ID") String systemId) {
        log.info("Fetching all rates for user with id {}", systemId);
        ResponseDto alerts = alertsStubService.getAlerts(systemId);
        if (!alerts.isSuccess()) {
            return ResponseEntity.status(alerts.getStatus()).build();
        }
        return ResponseEntity.ok(alerts.getAlertResponseDtoFlux());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Alert>> getAllAlerts(@RequestHeader("X-User-ID") String systemId) {
        log.info("Fetching all alerts for user with id {}", systemId);
        ResponseDto response = alertsStubService.getAllAlerts(systemId);
        if (!response.isSuccess()) {
            return ResponseEntity.status(response.getStatus()).build();
        }
        return ResponseEntity.ok(response.getAlerts());
    }
}
