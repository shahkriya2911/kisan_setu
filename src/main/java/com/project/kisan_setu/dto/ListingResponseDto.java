package com.project.kisan_setu.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ListingResponseDto {

    private Long listingId;

    // Product Info
    private String cropName;
    private String variety;
    private String grade;
    private LocalDate harvestDate;

    // Pricing
    private Integer quantity;
    private String unit;
    private Double basePrice;
    private String purchaseType;
    private Double minimumBidIncrement;
    private String saleType;
    private LocalDate auctionEndDate;

    // Location
    private String moisture;
    private String state;
    private String packagingType;
    private String district;
    private String storageType;
    private String pickupMethod;

    // Images & Certificate
    private List<ProductImageResponseDto> images;
    private QualityCertificateResponseDto certificate;
}

