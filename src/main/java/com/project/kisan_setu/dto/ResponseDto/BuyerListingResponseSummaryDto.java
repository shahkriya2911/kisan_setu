package com.project.kisan_setu.dto.ResponseDto;

import com.project.kisan_setu.enums.PurchaseType;
import com.project.kisan_setu.enums.SaleType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BuyerListingResponseSummaryDto {
    private Long listingId;
    private String cropName;
    private String variety;
    private String state;
    private BigDecimal quantity;
    private String unit;
    private String packagingType;
    private SaleType saleType;
    private String storageType;
    private LocalDate harvestDate;
    private BigDecimal minimumBidIncrement;
    private String grade;
    private BigDecimal totalBasePrice;
    private BigDecimal pricePerKg;
    private PurchaseType purchaseType;
    private String district;
    private LocalDateTime auctionEndTime;
    private BigDecimal currentHighestBid;
    private List<ProductImageResponseDto> images;
}
