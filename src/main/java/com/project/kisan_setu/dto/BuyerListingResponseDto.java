package com.project.kisan_setu.dto;

import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.enums.PurchaseType;
import com.project.kisan_setu.enums.SaleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuyerListingResponseDto {

    private Long listingId;
    private String cropName;
    private String variety;
    private String state;
    private String packagingType;
    private SaleType saleType;
    private String storageType;
    private LocalDate harvestDate;
    private BigDecimal minimumBidIncrement;
    private String grade;
    private BigDecimal basePrice;
    private BigDecimal pricePerKg;
    private PurchaseType purchaseType;
    private String district;
    private LocalDateTime auctionEndTime;
    private BigDecimal currentHighestBid;
    private List<ProductImageResponseDto> images;
}
