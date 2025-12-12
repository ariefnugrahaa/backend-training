package com.bcafinance.training.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PlafondRequest {
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String type;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal maxAmount;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal interestRate;
}
