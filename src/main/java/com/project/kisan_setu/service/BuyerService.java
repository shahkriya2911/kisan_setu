package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.RequestDto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.RequestDto.PlaceBidRequestDto;
import com.project.kisan_setu.dto.ResponseDto.BidResponseDto;
import com.project.kisan_setu.dto.ResponseDto.BuyerListingResponseDto;
import com.project.kisan_setu.dto.ResponseDto.BuyerRequirementSummaryDto;
import com.project.kisan_setu.dto.ResponseDto.BuyingRequirementResponseDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BuyerService {

    BuyingRequirementResponseDto postRequirement(BuyingRequirementRequestDto dto);

    Page<BuyingRequirementResponseDto> getMyRequirements(Pageable pageable, String cropName);

    BuyerRequirementSummaryDto getMyRequirementSummary();

    void deleteRequirement(Long requirementId);

    Page<BuyerListingResponseDto> getActiveAuctionListings(Long userId, Pageable pageable, String cropName);

    Page<BuyerListingResponseDto> getActiveFixedListings(Long userId, Pageable pageable, String cropName);

    @Transactional
    void closeExpiredListings();

    BuyerListingResponseDto getAuctionListingDetail(Long listingId);

    BidResponseDto placeBid(
            Long listingId,
            PlaceBidRequestDto dto);

    BuyerListingResponseDto getFixedListingDetail(Long listingId);
}
