package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SellerListingDto {
    private Long listingId;
    private String cropName;
    private String grade;
    private Integer quantity;
    private String unit;
    private Double totalBasePrice;
    private Double minimumBidIncrement;
    private String state;
    private String district;
    private LocalDateTime auctionEndTime;
    // Bid Info
    private Double currentHighestBid;
    private Long activeBidders;
    private List<BidResponseDto> top5Bids;
}
