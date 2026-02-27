package com.project.kisan_setu.dto;

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

    private Integer quantity;

    private Double pricePerKg;

    private BigDecimal totalBasePrice;

    private LocalDateTime orderTime;
    private Double remainingQuantity;
}