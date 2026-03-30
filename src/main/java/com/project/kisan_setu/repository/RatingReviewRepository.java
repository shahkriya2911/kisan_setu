package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.RatingAndReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingReviewRepository extends JpaRepository<RatingAndReview,Long> {

    boolean existsByOrder_OrderIdAndBuyer_UserId(Long orderId, Long userId);
    boolean existsByOrder_OrderIdAndSeller_UserId(Long orderId, Long userId);
}
