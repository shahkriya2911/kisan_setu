package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.RatingAndReview;
import com.project.kisan_setu.enums.ReviewType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingReviewRepository extends JpaRepository<RatingAndReview,Long> {

    boolean existsByOrder_OrderIdAndReviewType(Long orderId, ReviewType reviewType);
}
