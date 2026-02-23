package com.project.kisan_setu.dto;

import com.project.kisan_setu.enums.SaleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QualityPricingListingDto {
    private Integer quantity;
    private String unit;
    // Whole Lot Only / Partial Orders Allowed
    private String purchaseType;
    // FIXED PRICE Fields
    private Integer minimumOrderQuantity;
    private Double moqPricePerKg;
    // AUCTION Fields
    private Double pricePerKg;
    private Double totalBasePrice;
    private Double minimumBidIncrement;
    private SaleType saleType;  // FIXED or AUCTION
    private LocalDateTime auctionEndTime;

}
