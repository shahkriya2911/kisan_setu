package com.project.kisan_setu.service;

import com.project.kisan_setu.entity.Notification;
import com.project.kisan_setu.entity.User;

import java.util.List;

public interface NotificationService {
    public void notifyUser(User user, String message);
    List<Notification> getBuyerNotifications(User buyer);
}
