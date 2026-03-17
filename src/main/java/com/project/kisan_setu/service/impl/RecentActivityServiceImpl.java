package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.ResponseDto.RecentActivityDto;
import com.project.kisan_setu.entity.RecentActivity;
import com.project.kisan_setu.repository.RecentActivityRepository;
import com.project.kisan_setu.service.RecentActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecentActivityServiceImpl implements RecentActivityService {
    private final RecentActivityRepository recentActivityRepository;


    @Override
    public RecentActivityDto toDto(RecentActivity activity) {
        return new RecentActivityDto(
                activity.getActivityType(),
                activity.getDescription(),
                activity.getSellerId(),
                activity.getBuyerId(),
                activity.getTimestamp()
        );
    }

    @Override
    public List<RecentActivityDto> getUserRecentActivity(Long userId) {
        return recentActivityRepository
                .findTop50BySellerIdOrBuyerIdOrderByTimestampDesc(userId, userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public RecentActivityDto logActivity(String activityType, String description, Long sellerId, Long buyerId) {
        RecentActivity activity = new RecentActivity();
        activity.setActivityType(activityType);
        activity.setDescription(description);
        activity.setSellerId(sellerId);
        activity.setBuyerId(buyerId);
        activity.setTimestamp(LocalDateTime.now());

        RecentActivity saved = recentActivityRepository.save(activity);
        return toDto(saved);
    }

    @Override
    public List<RecentActivityDto> getAllRecentActivity() {
        return recentActivityRepository
                .findTop50ByOrderByTimestampDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


}
