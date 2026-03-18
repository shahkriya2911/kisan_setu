package com.project.kisan_setu.dto.ResponseDto;

import lombok.*;

import java.math.BigDecimal;
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
    private BigDecimal quantity;
    private BigDecimal pricePerKg;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
}