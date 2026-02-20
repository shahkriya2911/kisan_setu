package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.ProductImage;
import com.project.kisan_setu.entity.QualityCertificate;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListingMapper {

    private static final String BASE_URL = "http://localhost:8080/uploads/";

    public static ListingResponseDto toResponse(Listing listing) {

        ListingResponseDto dto = new ListingResponseDto();

        dto.setListingId(listing.getListingId());
        dto.setCropName(listing.getCropName());
        dto.setVariety(listing.getVariety());
        dto.setGrade(listing.getGrade());
        dto.setHarvestDate(listing.getHarvestDate());

        dto.setQuantity(listing.getQuantity());
        dto.setUnit(listing.getUnit());
        dto.setPricePerKg(listing.getPricePerKg());
        dto.setTotalBasePrice(listing.getTotalBasePrice());
        dto.setPurchaseType(listing.getPurchaseType());
        dto.setMinimumBidIncrement(listing.getMinimumBidIncrement());
        dto.setSaleType(listing.getSaleType().name());
        dto.setAuctionEndTime(listing.getAuctionEndTime());

        dto.setMoisture(listing.getMoisture());
        dto.setState(listing.getState());
        dto.setPackagingType(listing.getPackagingType());
        dto.setDistrict(listing.getDistrict());
        dto.setStorageType(listing.getStorageType());
        dto.setPickupMethod(listing.getPickupMethod());

        //  Images (Primary First)
        if (listing.getImages() != null) {

            List<ProductImageResponseDto> imageDtos =
                    listing.getImages()
                            .stream()
                            .sorted(Comparator.comparing(
                                    ProductImage::getIsPrimary,
                                    Comparator.nullsLast(Comparator.reverseOrder())
                            ))
                            .map(image -> {
                                ProductImageResponseDto imageDto =
                                        new ProductImageResponseDto();

                                imageDto.setImageId(image.getImageId());
                                imageDto.setIsPrimary(image.getIsPrimary());
                                imageDto.setImageUrl(
                                        BASE_URL + "productimage/" + image.getFileName()
                                );

                                return imageDto;
                            })
                            .collect(Collectors.toList());

            dto.setImages(imageDtos);
        }

        //  Certificate
        if (listing.getCertificate() != null) {

            QualityCertificate cert = listing.getCertificate();

            QualityCertificateResponseDto certDto =
                    new QualityCertificateResponseDto();

            certDto.setCertificateId(cert.getCertificateId());
            certDto.setCertificateName(cert.getCertificateName());
            certDto.setIssuedDate(cert.getIssuedDate());
            certDto.setCertificateUrl(
                    BASE_URL + "certificates/" + cert.getFileName()
            );

            dto.setCertificate(certDto);
        }

        return dto;
    }

    public static Listing toEntity(
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto) {

        Listing listing = new Listing();

        // Product Details
        listing.setCropName(productDto.getCropName());
        listing.setVariety(productDto.getVariety());
        listing.setGrade(productDto.getGrade());
        listing.setHarvestDate(productDto.getHarvestDate());

        // Pricing Details
        listing.setQuantity(pricingDto.getQuantity());
        listing.setUnit(pricingDto.getUnit());
        listing.setPricePerKg(pricingDto.getPricePerKg());
        listing.setPurchaseType(pricingDto.getPurchaseType());
        listing.setMinimumBidIncrement(pricingDto.getMinimumBidIncrement());
        listing.setTotalBasePrice(pricingDto.getTotalBasePrice());
        listing.setSaleType(pricingDto.getSaleType());
        listing.setAuctionEndTime(pricingDto.getAuctionEndTime());

        // Location Details
        listing.setMoisture(locationDto.getMoisture());
        listing.setState(locationDto.getState());
        listing.setPackagingType(locationDto.getPackagingType());
        listing.setDistrict(locationDto.getDistrict());
        listing.setStorageType(locationDto.getStorageType());
        listing.setPickupMethod(locationDto.getPickupMethod());

        return listing;
    }

}
