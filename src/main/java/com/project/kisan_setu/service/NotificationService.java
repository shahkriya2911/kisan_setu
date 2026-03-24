package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.ResponseDto.NotificationResponseDto;
import com.project.kisan_setu.entity.*;
import com.project.kisan_setu.enums.NotificationStatus;

import java.util.List;

public interface NotificationService {
    public void createNotification(User user, String message, NotificationStatus type,
                                   Listing listing, Bid bid, Order order);
    List<NotificationResponseDto> getUserNotifications(Long userId);

    void markAsRead(Long id);


}
