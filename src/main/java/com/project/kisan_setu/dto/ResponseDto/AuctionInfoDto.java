package com.project.kisan_setu.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuctionInfoDto {

    private BigDecimal basePricePerKg;

    private BigDecimal minBidIncrement;
    private BigDecimal maxBidIncrement;

    private BigDecimal totalLotValue;

    private BigDecimal currentHighestBidPerKg;

    private BigDecimal currentHighestBidTotal;

    private String auctionEndsIn;
    private Integer numberOfBuyers;
}
