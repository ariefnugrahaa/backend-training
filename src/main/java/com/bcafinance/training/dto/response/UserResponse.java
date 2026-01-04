package com.bcafinance.training.dto.response;

import lombok.Data;
import java.util.Set;
import java.util.UUID;

@Data
@lombok.Builder
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private Set<String> roles;
}
