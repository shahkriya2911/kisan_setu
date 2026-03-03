package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.AuctionInfoDto;
import com.project.kisan_setu.entity.Listing;

import java.math.BigDecimal;

public class AuctionMapper {

    public static AuctionInfoDto todto(Listing listing){
        AuctionInfoDto dto = new AuctionInfoDto();
        dto.setBasePricePerKg(listing.getPricePerKg());
        dto.setMinBidIncrement(listing.getMinimumBidIncrement());
        dto.setTotalLotValue(
                listing.getPricePerKg()
                        .multiply(listing.getQuantity())
        );

        return dto;
    }
}
