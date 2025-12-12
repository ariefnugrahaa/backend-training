package com.bcafinance.training.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private Set<String> roles;
}
