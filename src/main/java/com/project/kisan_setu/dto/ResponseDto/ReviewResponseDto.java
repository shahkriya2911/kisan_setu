package com.project.kisan_setu.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto {
    private Long reviewId;
    private Integer rating;
    private String review;

    private Long orderId;

    private Long buyerId;
    private String buyerName;

    private Long sellerId;
    private String sellerName;

    private Boolean isBuyerReview;
    private Boolean isSellerReview;

    private String createdAt;
}
