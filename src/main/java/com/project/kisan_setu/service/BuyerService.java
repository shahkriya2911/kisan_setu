package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.*;

import java.util.List;

public interface BuyerService {

    public BuyingRequirementResponseDto postRequirement(BuyingRequirementRequestDto dto) ;


        List<BuyerListingResponseDto> getActiveAuctionListings();

    // Place Bid
    Object placeBid(
            Long listingId,
            PlaceBidRequestDto dto);

    Object getBidHistory(Long listingId);

//    List<BidResponseDto> getBidHistory(Long listingId);
}
