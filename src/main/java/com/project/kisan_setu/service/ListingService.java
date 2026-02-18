package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.ListingResponseDto;
import com.project.kisan_setu.dto.ProductListingDto;
import com.project.kisan_setu.dto.QualityLocationListingDto;
import com.project.kisan_setu.dto.QualityPricingListingDto;
import com.project.kisan_setu.entity.Listing;

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
}
