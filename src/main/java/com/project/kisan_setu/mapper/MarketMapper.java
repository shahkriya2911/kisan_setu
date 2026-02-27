package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.MarketListingResponseDto;
import com.project.kisan_setu.entity.Listing;
import org.springframework.stereotype.Component;

@Component
public class MarketMapper {

    public MarketListingResponseDto toDto(Listing listing, Double highestBid) {

        return new MarketListingResponseDto(
                listing.getListingId(),
                listing.getCropName(),
                listing.getQuantity(),
                listing.getUnit(),
                listing.getSaleType(),
                listing.getTotalBasePrice() * listing.getQuantity(),
                listing.getTotalBasePrice(),
                listing.getDistrict(),
                listing.getState(),
                highestBid,
                listing.getAuctionEndTime(),
                "Whole Lot".equalsIgnoreCase(String.valueOf(listing.getPurchaseType()))
        );
    }
}
