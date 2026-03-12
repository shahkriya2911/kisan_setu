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

    private String buyerName;

    private String cropName;

    private BigDecimal quantity;

    private BigDecimal pricePerKg;

    private BigDecimal totalBasePrice;

    private LocalDateTime orderTime;
    private BigDecimal remainingQuantity;
}