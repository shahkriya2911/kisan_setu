package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.ResponseDto.CommodityListingDto;

import java.util.List;

public interface AdminCommodityService {
    List<CommodityListingDto> getAllCommodityListings();
    List<CommodityListingDto> getActiveCommodityListings();
    List<CommodityListingDto> getPendingCommodityListings();
    List<CommodityListingDto> getCompletedCommodityListings();
    String rejectListing(Long listingId);
}
