package com.project.kisan_setu.dto.ResponseDto;

import lombok.*;
import java.time.LocalDateTime;

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
    private String escrowStatus;
    private String otp;
    private Boolean otpVerified;
    private LocalDateTime createdAt;
}
