package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BidResponseDto {

    //bid response info
    private Long buyerId;
    private BigDecimal buyerAmount;
    private String buyerName;
    private LocalDateTime bidTime;
    private Double remainingQuantity;



}
