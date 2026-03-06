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
    private BigDecimal quantity;
        //AUCTION
    private BigDecimal buyerAmount;

}