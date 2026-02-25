package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.embedded.ListingCertificate;
import com.project.kisan_setu.embedded.ListingImage;
import com.project.kisan_setu.entity.Listing;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ListingMapper {

    // ===================== ENTITY → RESPONSE =====================

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

        return dto;
    }

    // ===================== DTO → ENTITY =====================

    public static Listing toEntity(
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto,
            List<ListingImage> images,              // already prepared in service
            ListingCertificate certificate          // already prepared in service
    ) {

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

        return listing;
    }

    // ===================== UPDATE EXISTING ENTITY =====================

    public static void updateEntity(
            Listing listing,
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto) {

        listing.setCropName(productDto.getCropName());
        listing.setVariety(productDto.getVariety());
        listing.setGrade(productDto.getGrade());
        listing.setHarvestDate(productDto.getHarvestDate());

        listing.setQuantity(pricingDto.getQuantity());
        listing.setUnit(pricingDto.getUnit());
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
    }
}