package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.ResponseDto.MarketListingResponseDto;
import com.project.kisan_setu.entity.Listing;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MarketMapper {

    public MarketListingResponseDto toDto(Listing listing, BigDecimal highestBid) {

        return new MarketListingResponseDto(
                listing.getListingId(),
                listing.getCrop().getCropName(),
                listing.getQuantity(),
                listing.getUnit().getUnitName(),
                listing.getSaleType(),
                listing.getTotalBasePrice().multiply(listing.getQuantity()),
                listing.getTotalBasePrice(),
                listing.getState() != null ? listing.getState().getName() : null,
                listing.getDistrict() != null ? listing.getDistrict().getName() : null,
                highestBid,
                listing.getAuctionEndTime(),
                "Whole Lot".equalsIgnoreCase(String.valueOf(listing.getPurchaseType()))
        );
    }
}
