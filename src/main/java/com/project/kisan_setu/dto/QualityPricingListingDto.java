package com.project.kisan_setu.dto;

import com.project.kisan_setu.enums.SaleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter //getters
@Setter //setters
@NoArgsConstructor //needed by jackson
public class QualityPricingListingDto {

    //quality and pricing info
    private Integer quantity;
    private String unit;
    private Double pricePerKg;
    private Double totalBasePrice;
    private String purchaseType;
    private Double minimumBidIncrement;
    @Enumerated(EnumType.STRING)
    private SaleType saleType;  // FIXED or AUCTION
    private LocalDateTime auctionEndTime;
}
