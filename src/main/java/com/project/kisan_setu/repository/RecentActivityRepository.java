package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.RecentActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RecentActivityRepository extends JpaRepository<RecentActivity,Long> {
    List<RecentActivity> findTop50BySellerUserIdOrBuyerUserIdOrderByTimestampDesc(Long userId, Long userId1);

    List<RecentActivity> findTop50ByOrderByTimestampDesc();
}
