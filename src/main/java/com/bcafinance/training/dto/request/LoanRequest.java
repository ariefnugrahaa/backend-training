package com.bcafinance.training.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanRequest {
    @NotNull
    private Long plafondId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    @NotNull
    @Min(1)
    private Integer tenor;
}
