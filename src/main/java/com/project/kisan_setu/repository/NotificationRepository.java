package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByUserUserId(Long userId);
}
