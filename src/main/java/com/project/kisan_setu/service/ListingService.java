package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.RequestDto.CreateListingRequest;
import com.project.kisan_setu.dto.RequestDto.ExtendAuctionDto;
import com.project.kisan_setu.dto.ResponseDto.BuyerContactResponseDto;
import com.project.kisan_setu.dto.ResponseDto.BuyingRequirementResponseDto;
import com.project.kisan_setu.dto.ResponseDto.DashboardDto;
import com.project.kisan_setu.dto.ResponseDto.ListingResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ListingSummaryResponseDto;
import com.project.kisan_setu.dto.ResponseDto.RecentBidResponseDto;
import com.project.kisan_setu.dto.ResponseDto.SellerListingDto;
import com.project.kisan_setu.dto.ResponseDto.SellerListingFixedDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ListingService {

    ListingResponseDto createListing(CreateListingRequest request, List<MultipartFile> imageFiles,
            MultipartFile certificateFile);

    ListingResponseDto updateListing(Long listingId, CreateListingRequest request, List<MultipartFile> imageFiles,
            MultipartFile certificateFile);

    List<ListingResponseDto> getAllListings();

    ListingResponseDto getListingById(Long id);

    void deleteListing(Long listingId, Long sellerId);

    SellerListingDto getSellerAuctionListingDetail(Long listingId);

    SellerListingFixedDto getSellerFixedListingDetail(Long listingId);

    DashboardDto getSellerOverview();

    Page<ListingResponseDto> activeListings(Long sellerId, Pageable pageable, String search);

    Page<Object> activeSummaryListings(Long sellerId, Pageable pageable, String cropName);

    Page<ListingResponseDto> pendingListings(Long sellerId, Pageable pageable, String search);

    Page<ListingResponseDto> soldListings(Long sellerId, Pageable pageable, String search);

    Page<ListingResponseDto> closedListings(Long sellerId, Pageable pageable, String search);

    String extendAuctionTime(ExtendAuctionDto dto);

    List<BuyingRequirementResponseDto> getAllRequirementsForSeller(String cropName);

    BuyerContactResponseDto getBuyerContact(Long requirementId);

    List<RecentBidResponseDto> getRecentBids();

    // Page<ListingSummaryResponseDto> activeSummaryListings(Long sellerId, Pageable
    // pageable);

    Page<ListingSummaryResponseDto> pendingSummaryListings(Long sellerId, Pageable pageable);

    Page<ListingSummaryResponseDto> soldSummaryListings(Long sellerId, Pageable pageable);

    Page<ListingSummaryResponseDto> closedSummaryListings(Long sellerId, Pageable pageable);

    Page<ListingResponseDto> myListings(Long userId, Pageable pageable, String search);
}
