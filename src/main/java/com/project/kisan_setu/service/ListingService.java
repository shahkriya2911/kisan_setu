package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.BidHistory;

import java.util.List;

public interface ListingService {

    ListingResponseDto createListing(
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto);

    List<ListingResponseDto> getAllListings();

    ListingResponseDto getListingById(Long id);

    void deleteListing(Long id);

    ListingResponseDto previewListing(
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto);

    BidHistory placeBid(Long listingId, Double bidAmount, Long userId);

    SellerListingDto getListingTop5BidDetail(Long listingId);
    DashboardDto getSellerOverview();
}