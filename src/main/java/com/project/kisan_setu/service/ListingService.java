package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.BidHistory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ListingService {

    BidHistory placeBid(Long listingId, Double bidAmount, Long userId);
    ListingResponseDto createListing(CreateListingRequest request, List<MultipartFile> imageFiles, MultipartFile certificateFile);
    ListingResponseDto updateListing(Long listingId, CreateListingRequest request, List<MultipartFile> imageFiles, MultipartFile certificateFile);
    List<ListingResponseDto> getAllListings();
    ListingResponseDto getListingById(Long id);
    void deleteListing(Long id);
    ListingResponseDto previewListing(ProductListingDto product, QualityPricingListingDto pricing, QualityLocationListingDto location);
    SellerListingDto getListingTop5BidDetail(Long listingId);
    DashboardDto getSellerOverview();
    }