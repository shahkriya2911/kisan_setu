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
    public void notifyBuyer(User buyer, String message) {
        Notification notification = new Notification();
        notification.setBuyer(buyer);
        notification.setMessage(message);
        notification.setBuyer(buyer);
        notificationRepository.save(notification);
        //print in console
        System.out.println("Notification to"+buyer.getFullName()+" : "+message);

    }
    public List<Notification> getBuyerNotifications(User buyer) {
        return notificationRepository.findByBuyer(buyer);
    }
}

