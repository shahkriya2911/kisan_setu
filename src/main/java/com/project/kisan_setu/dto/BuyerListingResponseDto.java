package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuyerListingResponseDto {

    private Long listingId;
    private String cropName;
    private String grade;
    private Double basePrice;
    private Double currentHighestBid;
    private String district;
    private LocalDateTime auctionEndTime;
}
