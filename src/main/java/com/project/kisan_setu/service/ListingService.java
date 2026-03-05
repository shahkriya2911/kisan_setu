package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.BidHistory;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ListingService {

    BidHistory placeBid(Long listingId, BigDecimal buyerAmount, Long userId);
    ListingResponseDto createListing(CreateListingRequest request, List<MultipartFile> imageFiles, MultipartFile certificateFile);
    ListingResponseDto updateListing(Long listingId, CreateListingRequest request, List<MultipartFile> imageFiles, MultipartFile certificateFile);
    List<ListingResponseDto> getAllListings();
    ListingResponseDto getListingById(Long id);
    void deleteListing(Long listingId,Long sellerId);

//    BidHistory placeBid(Long listingId, Double buyerAmount, Long userId);

    SellerListingDto getSellerListingDetail(Long listingId);
    DashboardDto getSellerOverview();
    Order acceptInqury(Long inquiryId, String email);
    ListingResponseDto updateListing(
            Long listingId,
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto);
    void markAsSold(Long listingId,Long userId);
    void extendAuctionTime(Long listingId,Long sellerId,int minutes);
    List<BuyingRequirementResponseDto> getBuyerRequirementsForSeller();

}
