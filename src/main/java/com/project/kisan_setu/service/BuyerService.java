package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.*;

import java.util.List;

public interface BuyerService {

    public BuyingRequirementResponseDto postRequirement(BuyingRequirementRequestDto dto) ;


        List<BuyerListingResponseDto> getActiveAuctionListings(Long userId);

    List<BuyerListingResponseDto> getActiveFixedListings(Long userId);

    BuyerListingResponseDto getAuctionListingDetail(Long listingId);

    // Place Bid
    Object placeBid(
            Long listingId,
            PlaceBidRequestDto dto);

    List<BidHistoryDto> getBidHistory(Long listingId);

    ListingSummaryResponseDto getListingSummary(Long listingId);
}
