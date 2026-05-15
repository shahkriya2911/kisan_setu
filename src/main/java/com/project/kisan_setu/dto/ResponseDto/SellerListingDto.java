package com.project.kisan_setu.dto.ResponseDto;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.PurchaseType;
import com.project.kisan_setu.enums.SaleType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SellerListingDto {

    private Long listingId;
    private String cropName;
    private String variety;
    private String grade;
    private BigDecimal quantity;
    private BigDecimal pricePerKg;
    private String unit;

    private BigDecimal totalBasePrice;
    private BigDecimal minimumBidIncrement;
    private BigDecimal maximumBidIncrement;

    private Long totalBids;
    private Long activeBidders;

    private AuctionStatus status;
    private LocalDateTime auctionEndTime;

    private BigDecimal currentHighestBid;
    private PurchaseType purchaseType;
    private SaleType saleType;
    private LocalDateTime postedOn;
    private String state;
    private String district;
    private String pickupMethod;
    private String storage;

    private List<BidResponseDto> top5Bids;
    private Long totalInquires;
    private List<ProductImageResponseDto> images;
    private LocalDate harvestDate;
    private String packagingType;
    private Long sellerId;
}
