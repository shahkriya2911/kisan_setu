package com.project.kisan_setu.dto;

import com.project.kisan_setu.enums.SaleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MarketListingResponseDto {

    private Long listingId;
    private String cropName;
    private Integer quantity;
    private String unit;
    private SaleType saleType;

    private Double totalPrice;
    private Double perUnitPrice;

    private String district;
    private String state;

    private Double highestBid;      // only for auction
    private LocalDateTime auctionEndDate;

    private Boolean wholeLot;
}
