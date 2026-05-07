package com.project.kisan_setu.dto.ResponseDto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private Long listingId;
    private Long buyerId;
    private Long sellerId;
    private String status;
    private BigDecimal quantity;
    private BigDecimal totalBasePrice;
    private String escrowStatus;
    private String otp;
    private Boolean otpVerified;
    private LocalDateTime createdAt;
    private List<ProductImageResponseDto> images;
}
