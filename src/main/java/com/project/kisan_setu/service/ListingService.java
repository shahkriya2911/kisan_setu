package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.BidHistory;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ListingService {

    BidHistory placeBid(Long listingId, BigDecimal buyerAmount, Long userId);
    ListingResponseDto createListing(CreateListingRequest request, List<MultipartFile> imageFiles, MultipartFile certificateFile);
    ListingResponseDto updateListing(Long listingId, CreateListingRequest request, List<MultipartFile> imageFiles, MultipartFile certificateFile);
    List<ListingResponseDto> getAllListings();
    ListingResponseDto getListingById(Long id);
    void deleteListing(Long id);

//    public ListingResponseDto previewListing(CreateListingRequest request);

//    BidHistory placeBid(Long listingId, Double buyerAmount, Long userId);

    SellerListingDto getSellerListingDetail(Long listingId);
    DashboardDto getSellerOverview();
    ListingResponseDto updateListing(
            Long listingId,
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto);
}