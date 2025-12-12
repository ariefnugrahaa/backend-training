package com.bcafinance.training.service;

import com.bcafinance.training.dto.response.UserResponse;
import com.bcafinance.training.entity.User;
import com.bcafinance.training.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUserProfile(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .build();
    }
}
