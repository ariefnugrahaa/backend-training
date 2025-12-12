package com.bcafinance.training.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class LoanResponse {
    private Long id;
    private UUID userId;
    private String userName;
    private Long plafondId;
    private String plafondName;
    private BigDecimal amount;
    private Integer tenor;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
