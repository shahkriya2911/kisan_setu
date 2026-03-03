package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuyerListingResponseDto {

    private Long listingId;
    private String cropName;
    private String grade;
    private BigDecimal basePrice;
    private BigDecimal currentHighestBid;
    private String district;
    private LocalDateTime auctionEndTime;


}
