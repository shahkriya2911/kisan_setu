package com.project.kisan_setu.controller;

import com.project.kisan_setu.entity.Notification;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.repository.NotificationRepository;
import com.project.kisan_setu.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    public NotificationController(NotificationService notificationService, NotificationRepository notificationRepository) {
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/{userId}")
    public List<Notification> getUserNotifications(
            @PathVariable Long userId) {
        logger.debug("Get notifications for user with id : {} request attempt",userId);
        User user = new User();
        user.setUserId(userId);
        logger.info("Notifications fetched successfully");
        return notificationService.getBuyerNotifications(user);

    }
}
