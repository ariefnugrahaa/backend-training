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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }

    @Transactional
    public MessageResponse registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .fullName(signUpRequest.getFullName())
                .phoneNumber(signUpRequest.getPhoneNumber())
                .address(signUpRequest.getAddress())
                .build();

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.NASABAH)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                try {
                    ERole eRole = ERole.valueOf(role.toUpperCase());
                    Role adminRole = roleRepository.findByName(eRole)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                } catch (IllegalArgumentException e) {
                    // Default or handle error. For safety, fail?
                    // Or just ignore invalid roles during dev?
                    // Logic: if not found, throw.
                    throw new RuntimeException("Error: Role " + role + " is not found");
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new MessageResponse("User registered successfully!");
    }
}
