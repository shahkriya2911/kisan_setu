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
<<<<<<< Updated upstream
    // Whole Lot Only / Partial Orders Allowed
=======
    private Double pricePerKg;
    private Double totalBasePrice;
>>>>>>> Stashed changes
    private String purchaseType;
    // FIXED PRICE Fields
    private Integer minimumOrderQuantity;
    private Double moqPricePerKg;
    private SaleType saleType;  // FIXED or AUCTION
    // Fixed Price OR Auction Base Price
    private Double pricePerKg;
    private Double totalBasePrice;
    private Double minimumBidIncrement;
    private LocalDateTime auctionEndTime;
}
