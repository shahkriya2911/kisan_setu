package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.ResponseDto.NotificationResponseDto;
import com.project.kisan_setu.repository.NotificationRepository;
import com.project.kisan_setu.service.NotificationService;
import com.project.kisan_setu.util.ValidatorMethods;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/notifications")
@Tag(name = "Notification Management", description = "Endpoints for notification related resources")
public class NotificationController {
    private final NotificationService notificationService;
    private final ValidatorMethods validatorMethods;
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    public NotificationController(NotificationService notificationService, ValidatorMethods validatorMethods, NotificationRepository notificationRepository) {
        this.notificationService = notificationService;
        this.validatorMethods = validatorMethods;
    }

    @GetMapping
    @Operation(summary = "Get user notifications method", description = "Used by user to get all notifications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public List<NotificationResponseDto> getUserNotifications() {
        Long userId = validatorMethods.getCurrentUserId();
        logger.debug("Get notifications for user with id : {} request attempt",userId);
        logger.info("Notifications fetched successfully");
        return notificationService.getUserNotifications(userId);
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark notification as read method", description = "Used by user to mark a notification as read")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification marked as read successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public void markAsRead(
            @Parameter(description = "Notification ID request", required = true)
            @PathVariable Long id){
        notificationService.markAsRead(id);
    }
}
