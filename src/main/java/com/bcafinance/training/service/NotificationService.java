package com.bcafinance.training.service;

import com.bcafinance.training.dto.response.NotificationResponse;
import com.bcafinance.training.entity.Notification;
import com.bcafinance.training.entity.User;
import com.bcafinance.training.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void send(User user, String title, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getMyNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(n -> NotificationResponse.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .message(n.getMessage())
                        .isRead(n.isRead())
                        .createdAt(n.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
