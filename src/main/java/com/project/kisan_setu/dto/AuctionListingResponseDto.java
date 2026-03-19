package com.project.kisan_setu.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.kisan_setu.dto.ResponseDto.ProductImageResponseDto;
import com.project.kisan_setu.dto.ResponseDto.QualityCertificateResponseDto;
import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.PurchaseType;
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
@NoArgsConstructor
@AllArgsConstructor
public class AuctionListingResponseDto {
    private Long listingId;

    // Product Info
    private String cropName;
    private String variety;
    private String grade;
    private LocalDate harvestDate;
    private BigDecimal quantity;
    private String unit;

    // Pricing & Purchase Type
    private BigDecimal pricePerKg;
    private BigDecimal totalBasePrice;
    private PurchaseType purchaseType;
    private String saleType;

    // Partial Orders fields
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal minimumOrderQuantity;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal moqPricePerKg;
    private BigDecimal remainingQuantity;

    // Auction fields
    private BigDecimal minimumBidIncrement;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime auctionEndTime;

    // Location

    private String state;
    private String packagingType;
    private String district;
    private String storageType;
    private String pickupMethod;

    // Images & Certificate
    private List<ProductImageResponseDto> images;
    private QualityCertificateResponseDto certificate;
    private String description;

    private LocalDateTime createdAt;
    private AuctionStatus auctionStatus;
    private BigDecimal highestBid;
    private String topBidderName;
    private Long topBid;
}
