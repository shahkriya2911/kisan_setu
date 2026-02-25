package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.NotificationResponseDto;
import com.project.kisan_setu.entity.Notification;

public class NotificationMapper {

    public static NotificationResponseDto toDto(Notification n){

        NotificationResponseDto dto =
                new NotificationResponseDto();

        dto.setNotificationId(n.getNotificationId());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setIsRead(n.getIsRead());
        dto.setCreatedTime(n.getCreatedTime());

        return dto;
    }

}