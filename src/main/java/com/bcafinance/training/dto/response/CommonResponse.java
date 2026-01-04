package com.bcafinance.training.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> {
    private Integer statusCode;
    private String message;
    private T data;
    private Object errors;
}
