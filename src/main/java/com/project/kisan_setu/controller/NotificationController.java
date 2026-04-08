package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.ResponseDto.NotificationResponseDto;
import com.project.kisan_setu.repository.NotificationRepository;
import com.project.kisan_setu.service.NotificationService;
import com.project.kisan_setu.util.ValidatorMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final ValidatorMethods validatorMethods;
    private final NotificationRepository notificationRepository;
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    public NotificationController(NotificationService notificationService, ValidatorMethods validatorMethods, NotificationRepository notificationRepository) {
        this.notificationService = notificationService;
        this.validatorMethods = validatorMethods;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public List<NotificationResponseDto> getUserNotifications() {
        Long userId = validatorMethods.getCurrentUserId();
        logger.debug("Get notifications for user with id : {} request attempt",userId);
        logger.info("Notifications fetched successfully");
        return notificationService.getUserNotifications(userId);
    }
    @PatchMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id){
        notificationService.markAsRead(id);
    }
}
