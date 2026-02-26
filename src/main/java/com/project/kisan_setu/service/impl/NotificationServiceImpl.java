package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.entity.Notification;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.repository.NotificationRepository;
import com.project.kisan_setu.repository.UserRepository;
import com.project.kisan_setu.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createNotification(Long userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Crop Created");
        notification.setMessage(message);

        notificationRepository.save(notification);

    }
    @Override
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserUserId(userId);
    }
}
