package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.ResponseDto.ReviewResponseDto;
import com.project.kisan_setu.entity.RatingAndReview;
import org.springframework.stereotype.Component;

@Component
public class RatingReviewMapper {
    public static ReviewResponseDto mapReviewToDto(RatingAndReview review) {

        if (review == null) {
            return null;
        }

        return ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .rating(review.getRating())
                .review(review.getReview())

                .orderId(review.getOrder() != null
                        ? review.getOrder().getOrderId()
                        : null)


                .buyerId(review.getBuyer() != null
                        ? review.getBuyer().getUserId()
                        : null)

                .buyerName(review.getBuyer() != null
                        ? review.getBuyer().getFullName()
                        : null)


                .sellerId(review.getSeller() != null
                        ? review.getSeller().getUserId()
                        : null)

                .sellerName(review.getSeller() != null
                        ? review.getSeller().getFullName()
                        : null)


                .isBuyerReview(review.getIsBuyerReview() != null
                        ? review.getIsBuyerReview()
                        : false)


                .createdAt(review.getCreatedAt() != null
                        ? review.getCreatedAt().toString()
                        : null)

                .build();
    }
}
