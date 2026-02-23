package com.project.kisan_setu.service;

<<<<<<< Updated upstream
import com.project.kisan_setu.dto.*;
=======
import com.project.kisan_setu.dto.ListingResponseDto;
import com.project.kisan_setu.dto.ProductListingDto;
import com.project.kisan_setu.dto.QualityLocationListingDto;
import com.project.kisan_setu.dto.QualityPricingListingDto;
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
    SellerListingDto getListingTop5BidDetail(Long listingId);

=======
>>>>>>> Stashed changes
}
