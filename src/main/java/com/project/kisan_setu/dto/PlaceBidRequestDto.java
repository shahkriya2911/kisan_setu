package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceBidRequestDto {
        //FIXED
    private Integer quantity;

    private Double pricePerKg;

    private Double totalBasePrice;

        //AUCTION
    private Double bidAmount;

}