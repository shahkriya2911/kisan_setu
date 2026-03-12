package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.entity.Notification;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.repository.NotificationRepository;
import com.project.kisan_setu.repository.UserRepository;
import com.project.kisan_setu.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void notifyUser(User user, String message) {
        logger.info("Notifying buyer....");
        Notification notification = new Notification();
        notification.setBuyer(user);
        notification.setIsRead(false);
        notification.setMessage(message);
        notificationRepository.save(notification);
        //print in console
        System.out.println("Notification to"+user.getFullName()+" : "+message);

    }
    public List<Notification> getBuyerNotifications(User buyer) {
        return notificationRepository.findByBuyer(buyer);
    }

    @Override
    public void notifyBuyer(User buyer, String message) {
    }
}

