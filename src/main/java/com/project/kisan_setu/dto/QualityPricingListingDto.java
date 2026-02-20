package com.project.kisan_setu.dto;

import com.project.kisan_setu.enums.SaleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class QualityPricingListingDto {
    private Integer quantity;
    private String unit;
    private Double basePrice;
    private String purchaseType;
    private Double minimumBidIncrement;
    @Enumerated(EnumType.STRING)
    private SaleType saleType;  // FIXED or AUCTION
    private LocalDateTime auctionEndTime;
}
