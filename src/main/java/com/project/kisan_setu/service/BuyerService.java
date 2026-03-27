package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.RequestDto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.RequestDto.PlaceBidRequestDto;
import com.project.kisan_setu.dto.ResponseDto.BuyerListingResponseDto;
import com.project.kisan_setu.dto.ResponseDto.BuyingRequirementResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ListingSummaryResponseDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BuyerService {

    public BuyingRequirementResponseDto postRequirement(BuyingRequirementRequestDto dto) ;


        List<BuyerListingResponseDto> getActiveAuctionListings(Long userId);

    List<BuyerListingResponseDto> getActiveFixedListings(Long userId);

    // =============================
    // CLOSE EXPIRED AUCTIONS
    // =============================
    @Transactional
    void closeExpiredListings();

    BuyerListingResponseDto getAuctionListingDetail(Long listingId);

    // Place Bid
    Object placeBid(
            Long listingId,
            PlaceBidRequestDto dto);

    ListingSummaryResponseDto getListingSummary(Long listingId);

    BuyerListingResponseDto getFixedListingDetail(Long listingId);
}
