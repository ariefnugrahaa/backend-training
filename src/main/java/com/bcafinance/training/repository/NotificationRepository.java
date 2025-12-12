package com.bcafinance.training.repository;

import com.bcafinance.training.entity.Notification;
import com.bcafinance.training.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}
