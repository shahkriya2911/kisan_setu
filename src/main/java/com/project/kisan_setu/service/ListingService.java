package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.RequestDto.CreateListingRequest;
import com.project.kisan_setu.dto.RequestDto.ExtendAuctionDto;
import com.project.kisan_setu.dto.ResponseDto.DashboardDto;
import com.project.kisan_setu.dto.ResponseDto.*;
import com.project.kisan_setu.dto.SellerListingFixedDto;
import com.project.kisan_setu.entity.Bid;

import com.project.kisan_setu.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ListingService {

    Bid placeBid(Long listingId, BigDecimal buyerAmount, Long userId);
    ListingResponseDto createListing(CreateListingRequest request, List<MultipartFile> imageFiles, MultipartFile certificateFile);
    ListingResponseDto updateListing(Long listingId, CreateListingRequest request, List<MultipartFile> imageFiles, MultipartFile certificateFile);
    List<ListingResponseDto> getAllListings();
    ListingResponseDto getListingById(Long id);
    void deleteListing(Long listingId,Long sellerId);

//    BidHistory placeBid(Long listingId, Double buyerAmount, Long userId);

    SellerListingDto getSellerAuctionListingDetail(Long listingId);
    SellerListingFixedDto getSellerFixedListingDetail(Long listingId);
    DashboardDto getSellerOverview();

    void markAsSold(Long listingId,Long userId);

    Page<ListingResponseDto> activeListings(Long sellerId, Pageable pageable);
    Page<Object> activeSummaryListings(Long sellerId, Pageable pageable);

    Page<ListingResponseDto> pendingListings(Long sellerId, Pageable pageable);

    Page<ListingResponseDto> soldListings(Long sellerId, Pageable pageable);

    Page<ListingResponseDto> closedListings(Long sellerId, Pageable pageable);

    String extendAuctionTime(ExtendAuctionDto dto);
    List<BuyingRequirementResponseDto> getBuyerRequirementsForSeller();
    BuyerContactResponseDto getBuyerContact(Long requirementId);
    List<RecentBidResponseDto> getRecentBids();

//    Page<ListingSummaryResponseDto> activeSummaryListings(Long sellerId, Pageable pageable);

    Page<ListingSummaryResponseDto> pendingSummaryListings(Long sellerId, Pageable pageable);

    Page<ListingSummaryResponseDto> soldSummaryListings(Long sellerId, Pageable pageable);

    Page<ListingSummaryResponseDto> closedSummaryListings(Long sellerId, Pageable pageable);

    Page<ListingResponseDto> myListings(Long userId, Pageable pageable);
}
