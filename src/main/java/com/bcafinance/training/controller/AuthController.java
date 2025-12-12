package com.bcafinance.training.controller;

import com.bcafinance.training.dto.request.LoginRequest;
import com.bcafinance.training.dto.request.SignupRequest;
import com.bcafinance.training.dto.response.JwtResponse;
import com.bcafinance.training.dto.response.MessageResponse;
import com.bcafinance.training.dto.response.UserResponse;
import com.bcafinance.training.entity.User;
import com.bcafinance.training.repository.UserRepository;
import com.bcafinance.training.security.services.UserDetailsImpl;
import com.bcafinance.training.service.AuthService;
import com.bcafinance.training.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserRepository userRepository; // Direct access for simplicity in profile, ideally service

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return ResponseEntity.ok(authService.registerUser(signUpRequest));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId()).orElseThrow();
        return ResponseEntity.ok(userService.getUserProfile(user));
    }
}
