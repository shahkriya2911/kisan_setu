package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.RatingAndReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingReviewRepository extends JpaRepository<RatingAndReview,Long> {

    boolean existsByOrder_OrderIdAndBuyerReview(Long orderId, Boolean buyerReview);

    boolean existsByOrder_OrderIdAndSellerReview(Long orderId, Boolean sellerReview);
}
