package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.RatingAndReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingReviewRepository extends JpaRepository<RatingAndReview,Long> {

    boolean existsByOrder_OrderIdAndIsBuyerReview(Long orderId, Boolean isBuyerReview);

    boolean existsByOrder_OrderIdAndIsSellerReview(Long orderId, Boolean isSellerReview);
}
