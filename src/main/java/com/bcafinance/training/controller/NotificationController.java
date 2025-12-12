package com.bcafinance.training.controller;

import com.bcafinance.training.dto.response.NotificationResponse;
import com.bcafinance.training.entity.User;
import com.bcafinance.training.repository.UserRepository;
import com.bcafinance.training.security.services.UserDetailsImpl;
import com.bcafinance.training.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId()).orElseThrow();
        return ResponseEntity.ok(notificationService.getMyNotifications(user));
    }
}
