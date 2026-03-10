package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.BidHistory;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.enums.BidStatus;
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

    SellerListingDto getSellerListingDetail(Long listingId);
    DashboardDto getSellerOverview();
    Order acceptInqury(Long inquiryId, Long userId);

    void markAsSold(Long listingId,Long userId);

    Page<ListingResponseDto> activeListings(Long sellerId, Pageable pageable);

    Page<ListingResponseDto> pendingListings(Long sellerId, Pageable pageable);

    Page<ListingResponseDto> soldListings(Long sellerId, Pageable pageable);

    Page<ListingResponseDto> closedListings(Long sellerId, Pageable pageable);

    void extendAuctionTime(Long listingId, Long sellerId, int minutes);
    List<BuyingRequirementResponseDto> getBuyerRequirementsForSeller();
    BuyerContactResponseDto getBuyerContact(Long requirementId);
    List<RecentBidResponseDto> getRecentBids();
    String acceptBid(Long bidId);
    String rejectBid(Long bidId);

}
