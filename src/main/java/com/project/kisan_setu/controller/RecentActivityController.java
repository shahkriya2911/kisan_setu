package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.ResponseDto.RecentActivityDto;
import com.project.kisan_setu.service.RecentActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class RecentActivityController {

    private final RecentActivityService recentActivityService;


    @GetMapping("/user/{userId}/recent")
    public List<RecentActivityDto> getUserRecentActivity(@PathVariable Long userId) {
        return recentActivityService.getUserRecentActivity(userId);
    }

    @PostMapping("/log")
    public RecentActivityDto logActivity(@RequestParam String activityType,
                                         @RequestParam String description,
                                         @RequestParam(required = false) Long sellerId,
                                         @RequestParam(required = false) Long buyerId) {
        return recentActivityService.logActivity(activityType, description, sellerId, buyerId);
    }
    @GetMapping("/admin/recent")
    public List<RecentActivityDto> getAllRecentActivity() {
        return recentActivityService.getAllRecentActivity();
    }
}