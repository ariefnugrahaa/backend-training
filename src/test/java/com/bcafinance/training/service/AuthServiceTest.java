package com.bcafinance.training.service;

import com.bcafinance.training.dto.request.LoginRequest;
import com.bcafinance.training.dto.request.SignupRequest;
import com.bcafinance.training.dto.response.JwtResponse;
import com.bcafinance.training.dto.response.MessageResponse;
import com.bcafinance.training.entity.ERole;
import com.bcafinance.training.entity.Role;
import com.bcafinance.training.entity.User;
import com.bcafinance.training.repository.RoleRepository;
import com.bcafinance.training.repository.UserRepository;
import com.bcafinance.training.security.jwt.JwtUtils;
import com.bcafinance.training.security.services.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    @Test
    void testAuthenticateUser_Success() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = new UserDetailsImpl(UUID.randomUUID(), "testuser", "test@example.com", "password",
                Collections.emptyList());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");

        // Act
        JwtResponse response = authService.authenticateUser(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password");
        request.setRole(Set.of("nasabah"));

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName(ERole.NASABAH)).thenReturn(Optional.of(new Role(1L, ERole.NASABAH, null)));
        when(encoder.encode(request.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        MessageResponse response = authService.registerUser(request);

        // Assert
        assertEquals("User registered successfully!", response.getMessage());
    }

    @Test
    void testRegisterUser_UsernameTaken() {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setUsername("existing");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> authService.registerUser(request));
        assertEquals("Error: Username is already taken!", exception.getMessage());
    }
}
