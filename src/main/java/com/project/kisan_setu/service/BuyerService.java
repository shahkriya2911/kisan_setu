package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.RequestDto.BidHistoryDto;
import com.project.kisan_setu.dto.RequestDto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.RequestDto.PlaceBidRequestDto;
import com.project.kisan_setu.dto.ResponseDto.BuyerListingResponseDto;
import com.project.kisan_setu.dto.ResponseDto.BuyingRequirementResponseDto;

import java.util.List;

public interface BuyerService {

    public BuyingRequirementResponseDto postRequirement(BuyingRequirementRequestDto dto) ;


        List<BuyerListingResponseDto> getActiveAuctionListings(Long userId);

    BuyerListingResponseDto getAuctionListingDetail(Long listingId);

    // Place Bid
    Object placeBid(
            Long listingId,
            PlaceBidRequestDto dto);

    List<BidHistoryDto> getBidHistory(Long listingId);
}
