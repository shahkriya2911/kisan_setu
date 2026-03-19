package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.ResponseDto.NotificationResponseDto;
import com.project.kisan_setu.entity.*;
import com.project.kisan_setu.enums.NotificationStatus;
import com.project.kisan_setu.mapper.NotificationMapper;
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
    public void createNotification(User user, String message, NotificationStatus type
    , Listing listing, Bid bid, Order order) {
        logger.info("Notifying buyer....");
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setListing(listing);
        notification.setBid(bid);
        notification.setOrder(order);
        notificationRepository.save(notification);
    }
    public List<NotificationResponseDto> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
        return notifications.stream().map(NotificationMapper::toDto).toList();
    }

    @Override
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.
                findById(id).orElseThrow(()->new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
}

