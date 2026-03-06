package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.embedded.ListingCertificate;
import com.project.kisan_setu.embedded.ListingImage;
import com.project.kisan_setu.entity.Listing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ListingMapper {

    private static final String BASE_URL = "http://localhost:8080/api/";

    public static ListingResponseDto toResponse(Listing listing) {

        ListingResponseDto dto = new ListingResponseDto();
        dto.setListingId(listing.getListingId());

        dto.setCropName(listing.getCropName());
        dto.setVariety(listing.getVariety());
        dto.setGrade(listing.getGrade());
        dto.setHarvestDate(listing.getHarvestDate());
        dto.setQuantity(listing.getQuantity());
        dto.setUnit(listing.getUnit());

        // Pricing
        dto.setPricePerKg(listing.getPricePerKg());
        dto.setTotalBasePrice(listing.getTotalBasePrice());
        dto.setMinimumBidIncrement(listing.getMinimumBidIncrement());
        dto.setPurchaseType(listing.getPurchaseType());
        dto.setSaleType(listing.getSaleType() != null
                ? listing.getSaleType().name()
                : null);
        dto.setAuctionEndTime(listing.getAuctionEndTime());

        // Partial Order
        dto.setMinimumOrderQuantity(listing.getMinimumOrderQuantity());
        dto.setMoqPricePerKg(listing.getMoqPricePerKg());
        dto.setRemainingQuantity(listing.getRemainingQuantity());

        // Location
        dto.setState(listing.getState());
        dto.setDistrict(listing.getDistrict());
        dto.setPackagingType(listing.getPackagingType());
        dto.setStorageType(listing.getStorageType());
        dto.setPickupMethod(listing.getPickupMethod());

        // Images
        if (listing.getImages() != null && !listing.getImages().isEmpty()) {

            List<ProductImageResponseDto> images =
                    listing.getImages().stream().map(img -> {

                        ProductImageResponseDto imgDto =
                                new ProductImageResponseDto();

                        imgDto.setFileName(img.getFileName());
                        imgDto.setFilePath(img.getFilePath());
                        imgDto.setFileType(img.getFileType());
                        imgDto.setIsPrimary(img.getIsPrimary());

                        return imgDto;

                    }).collect(Collectors.toList());

            dto.setImages(images);
        }

        // Certificate
        if (listing.getCertificate() != null) {

            QualityCertificateResponseDto certDto =
                    new QualityCertificateResponseDto();

            certDto.setFileName(listing.getCertificate().getFileName());
            certDto.setFilePath(listing.getCertificate().getFilePath());
            certDto.setFileType(listing.getCertificate().getFileType());

            dto.setCertificate(certDto);
        }

        //description
        dto.setDescription(listing.getDescription());

        dto.setCreatedAt(listing.getCreatedAt());

        return dto;
    }

    public static Listing toEntity(
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto,
            List<ListingImage> images,
            ListingCertificate certificate,
            String description) {

        Listing listing = new Listing();

        // Product
        listing.setCropName(productDto.getCropName());
        listing.setVariety(productDto.getVariety());
        listing.setGrade(productDto.getGrade());
        listing.setHarvestDate(productDto.getHarvestDate());

        // Pricing
        listing.setQuantity(pricingDto.getQuantity());
        listing.setUnit(pricingDto.getUnit());
        listing.setPricePerKg(pricingDto.getPricePerKg());
        listing.setTotalBasePrice(pricingDto.getTotalBasePrice());
        listing.setPurchaseType(pricingDto.getPurchaseType());
        listing.setMinimumBidIncrement(pricingDto.getMinimumBidIncrement());
        listing.setSaleType(pricingDto.getSaleType());
        listing.setAuctionEndTime(pricingDto.getAuctionEndTime());
        listing.setRemainingQuantity(pricingDto.getRemainingQuantity());

        // Partial Order
        listing.setMinimumOrderQuantity(pricingDto.getMinimumOrderQuantity());
        listing.setMoqPricePerKg(pricingDto.getMoqPricePerKg());

        // Location
        listing.setState(locationDto.getState());
        listing.setDistrict(locationDto.getDistrict());
        listing.setPackagingType(locationDto.getPackagingType());
        listing.setStorageType(locationDto.getStorageType());
        listing.setPickupMethod(locationDto.getPickupMethod());

        // Images (already mapped in service)
        listing.setImages(images);

        // Certificate (already created in service)
        listing.setCertificate(certificate);

        listing.setCreatedAt(LocalDateTime.now());

        listing.setDescription(description);

        return listing;
    }

    // UPDATE EXISTING ENTITY

    public static void updateEntity(
            Listing listing,
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto,
            String description) {

        listing.setCropName(productDto.getCropName());
        listing.setVariety(productDto.getVariety());
        listing.setGrade(productDto.getGrade());
        listing.setHarvestDate(productDto.getHarvestDate());

        listing.setQuantity(pricingDto.getQuantity());
        listing.setUnit(pricingDto.getUnit());
        listing.setRemainingQuantity(pricingDto.getRemainingQuantity());
        listing.setPurchaseType(pricingDto.getPurchaseType());
        listing.setMinimumOrderQuantity(pricingDto.getMinimumOrderQuantity());
        listing.setMoqPricePerKg(pricingDto.getMoqPricePerKg());
        listing.setPricePerKg(pricingDto.getPricePerKg());
        listing.setTotalBasePrice(pricingDto.getTotalBasePrice());
        listing.setMinimumBidIncrement(pricingDto.getMinimumBidIncrement());
        listing.setAuctionEndTime(pricingDto.getAuctionEndTime());
        listing.setSaleType(pricingDto.getSaleType());

        // Location
        listing.setState(locationDto.getState());
        listing.setDistrict(locationDto.getDistrict());
        listing.setPackagingType(locationDto.getPackagingType());
        listing.setStorageType(locationDto.getStorageType());
        listing.setPickupMethod(locationDto.getPickupMethod());

        listing.setDescription(description);
    }
}