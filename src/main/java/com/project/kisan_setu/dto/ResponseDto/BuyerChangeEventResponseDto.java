package com.project.kisan_setu.dto.ResponseDto;

import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.BidStatus;
import com.project.kisan_setu.enums.SaleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyerChangeEventResponseDto {
    private Long listingId;
    private SaleType saleType;
    private AuctionStatus auctionStatus;
    private BidStatus bidStatus;
    private BigDecimal currentHighestBid;
    private BidResponseDto bidResponseDto;
    private Long sellerId;
}
