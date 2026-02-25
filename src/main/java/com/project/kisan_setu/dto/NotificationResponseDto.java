package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationResponseDto {

    private Long notificationId;

    private String title;

    private String message;

    private Boolean isRead;

    private LocalDateTime createdTime;

}