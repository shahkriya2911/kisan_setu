package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.ResponseDto.RecentActivityDto;
import com.project.kisan_setu.entity.RecentActivity;

import java.util.List;

public interface RecentActivityService {
    RecentActivityDto toDto(RecentActivity activity);
    List<RecentActivityDto> getUserRecentActivity(Long userId);
    RecentActivityDto logActivity(String activityType, String description, Long sellerId, Long buyerId);
    List<RecentActivityDto> getAllRecentActivity();
}
