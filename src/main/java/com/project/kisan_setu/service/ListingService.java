package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.BidHistory;

import java.util.List;

public interface ListingService {

    public ListingResponseDto createListing(CreateListingRequest request);

    List<ListingResponseDto> getAllListings();

    ListingResponseDto getListingById(Long id);

    void deleteListing(Long id);

    public ListingResponseDto previewListing(CreateListingRequest request);

//    BidHistory placeBid(Long listingId, Double bidAmount, Long userId);

    SellerListingDto getListingTop5BidDetail(Long listingId);

    DashboardDto getSellerOverview();
}