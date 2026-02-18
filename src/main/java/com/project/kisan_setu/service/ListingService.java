package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.ProductListingDto;
import com.project.kisan_setu.dto.QualityLocationListingDto;
import com.project.kisan_setu.dto.QualityPricingListingDto;
import com.project.kisan_setu.entity.Listing;

import java.util.List;

public interface ListingService {

    Listing createListing(
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto);

    public List<Listing> getAllListings();
    public Listing getListingById(Long id);
    public void deleteListing(Long id);
    public Listing previewListing(
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto);




}
