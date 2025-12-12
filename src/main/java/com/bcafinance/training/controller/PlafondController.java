package com.bcafinance.training.controller;

import com.bcafinance.training.dto.request.PlafondRequest;
import com.bcafinance.training.dto.response.PlafondResponse;
import com.bcafinance.training.service.PlafondService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
public class PlafondController {

    private final PlafondService plafondService;

    @GetMapping("/api/public/plafonds")
    public ResponseEntity<List<PlafondResponse>> getAllPlafonds() {
        return ResponseEntity.ok(plafondService.getAll());
    }

    @PostMapping("/api/plafonds")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<PlafondResponse> createPlafond(@Valid @RequestBody PlafondRequest request) {
        return ResponseEntity.ok(plafondService.create(request));
    }
}
