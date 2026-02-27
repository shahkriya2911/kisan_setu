package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceBidRequestDto {
        //FIXED
    private Integer quantity;
    private Double pricePerKg;
    private BigDecimal totalBasePrice;
    private BigDecimal buyerAmount;

}