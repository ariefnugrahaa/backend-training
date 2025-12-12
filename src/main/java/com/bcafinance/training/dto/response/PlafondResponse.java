package com.bcafinance.training.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class PlafondResponse {
    private Long id;
    private String name;
    private String description;
    private String type;
    private BigDecimal maxAmount;
    private BigDecimal interestRate;
}
