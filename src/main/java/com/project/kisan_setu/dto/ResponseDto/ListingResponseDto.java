package com.project.kisan_setu.dto.ResponseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.PurchaseType;
import com.project.kisan_setu.enums.SaleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListingResponseDto {

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
    private SaleType saleType;

    // Partial Orders fields
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal minimumOrderQuantity;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal moqPricePerKg;

    // Auction fields
    private BigDecimal minimumBidIncrement;
    private BigDecimal maximumBidIncrement;
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
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private BigDecimal highestBid;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String topBidderName;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Long topBid;
    private Long bidId;
}
