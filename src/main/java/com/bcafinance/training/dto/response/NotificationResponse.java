package com.bcafinance.training.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
}
