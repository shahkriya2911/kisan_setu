package com.project.kisan_setu.service;
import com.project.kisan_setu.dto.RequestDto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.RequestDto.PlaceBidRequestDto;
import com.project.kisan_setu.dto.ResponseDto.BuyerListingResponseDto;
import com.project.kisan_setu.dto.ResponseDto.BuyingRequirementResponseDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface BuyerService {

    BuyingRequirementResponseDto postRequirement(BuyingRequirementRequestDto dto) ;

    Page<BuyerListingResponseDto> getActiveAuctionListings(Long userId,Pageable pageable);

    Page<BuyerListingResponseDto> getActiveFixedListings(Long userId, Pageable pageable);

    @Transactional
    void closeExpiredListings();

    BuyerListingResponseDto getAuctionListingDetail(Long listingId);


    Object placeBid(
            Long listingId,
            PlaceBidRequestDto dto);

    BuyerListingResponseDto getFixedListingDetail(Long listingId);
}
