package com.project.kisan_setu.dto;

import lombok.*;

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

    private Double totalBasePrice;

    private LocalDateTime orderTime;
    private Double remainingQuantity;
}