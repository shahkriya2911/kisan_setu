package com.project.kisan_setu.mapper;
import com.project.kisan_setu.dto.ProductListingDto;
import com.project.kisan_setu.dto.QualityPricingListingDto;
import com.project.kisan_setu.dto.QualityLocationListingDto;
import com.project.kisan_setu.entity.Listing;

public class ListingMapper {

    public static Listing toEntity(
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto) {

        Listing listing = new Listing();

        // 🔹 Product Info
        listing.setCropName(productDto.getCropName());
        listing.setVariety(productDto.getVariety());
        listing.setGrade(productDto.getGrade());
        listing.setHarvestDate(productDto.getHarvestDate());

        // 🔹 Quantity & Pricing
        listing.setQuantity(pricingDto.getQuantity());
        listing.setUnit(pricingDto.getUnit());
        listing.setBasePrice(pricingDto.getBasePrice());
        listing.setPurchaseType(pricingDto.getPurchaseType());
        listing.setMinimumBidIncrement(pricingDto.getMinimumBidIncrement());
        listing.setSaleType(pricingDto.getSaleType());
        listing.setAuctionEndDate(pricingDto.getAuctionEndDate());

        // 🔹 Quality & Location
        listing.setMoisture(locationDto.getMoisture());
        listing.setState(locationDto.getState());
        listing.setPackagingType(locationDto.getPackagingType());
        listing.setDistrict(locationDto.getDistrict());
        listing.setStorageType(locationDto.getStorageType());
        listing.setPickupMethod(locationDto.getPickupMethod());

        return listing;
    }
}

