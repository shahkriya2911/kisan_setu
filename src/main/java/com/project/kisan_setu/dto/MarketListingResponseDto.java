package com.project.kisan_setu.dto;

import com.project.kisan_setu.enums.SaleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MarketListingResponseDto {

    private Long listingId;
    private String cropName;
    private BigDecimal quantity;
    private String unit;
    private SaleType saleType;

    private BigDecimal totalPrice;
    private BigDecimal perUnitPrice;

    private String district;
    private String state;

    private BigDecimal highestBid;      // only for auction
    private LocalDateTime auctionEndTime;

    private Boolean wholeLot;
}
