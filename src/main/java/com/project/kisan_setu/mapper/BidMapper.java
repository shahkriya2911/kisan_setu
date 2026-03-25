package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.ResponseDto.MyBiddingsResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ProductImageResponseDto;
import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.Listing;

import java.math.BigDecimal;

public class BidMapper {
    public static MyBiddingsResponseDto toResponse(Bid bid, BigDecimal highestBid){
        MyBiddingsResponseDto dto = new MyBiddingsResponseDto();
        Listing listing = bid.getListing();
        dto.setCropName(listing.getCrop().getCropName());
        dto.setVariety(listing.getVariety());
        dto.setQuantity(listing.getQuantity());
        dto.setBidderName(bid.getBuyer().getFullName());
        dto.setBidAmount(bid.getBuyerAmount());
        dto.setPricePerKg(listing.getPricePerKg());
        dto.setState(listing.getState().getName());
        dto.setBidPlaced(bid.getCreatedAt());
        dto.setBidStatus(bid.getBidStatus());
        dto.setImages(ListingMapper.mapImages(listing));
        dto.setCurrentHighestBid(highestBid);

        return dto;
    }
}
