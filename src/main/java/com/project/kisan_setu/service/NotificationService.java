package com.project.kisan_setu.service;

import com.project.kisan_setu.entity.Notification;

import java.util.List;

public interface NotificationService {
    public void createNotification(Long userId, String message);
    List<Notification> getUserNotifications(Long userId);
}
