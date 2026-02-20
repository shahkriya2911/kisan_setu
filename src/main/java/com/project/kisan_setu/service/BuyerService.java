package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.*;

import java.util.List;

public interface BuyerService {

    BuyingRequirementResponseDto postRequirement(
            Long buyerId,
            BuyingRequirementRequestDto dto);

    List<BuyerListingResponseDto> getActiveAuctionListings();

    BidResponseDto placeBid(
            Long buyerId,
            Long listingId,
            PlaceBidRequestDto dto);

    List<BidResponseDto> getBidHistory(Long listingId);
}
