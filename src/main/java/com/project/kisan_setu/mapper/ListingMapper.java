package com.project.kisan_setu.mapper;
import com.project.kisan_setu.dto.AuctionListingResponseDto;
import com.project.kisan_setu.dto.ListingFixedResponseDto;
import com.project.kisan_setu.dto.RequestDto.ProductListingDto;
import com.project.kisan_setu.dto.RequestDto.QualityLocationListingDto;
import com.project.kisan_setu.dto.RequestDto.QualityPricingListingDto;
import com.project.kisan_setu.dto.ResponseDto.ListingResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ListingSummaryResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ProductImageResponseDto;
import com.project.kisan_setu.dto.ResponseDto.QualityCertificateResponseDto;
import com.project.kisan_setu.embedded.ListingCertificate;
import com.project.kisan_setu.embedded.ListingImage;
import com.project.kisan_setu.entity.*;
import com.project.kisan_setu.repository.BidRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ListingMapper {

    public static ListingResponseDto toResponse(Listing listing, Bid highesBid) {

        ListingResponseDto dto = new ListingResponseDto();
        dto.setListingId(listing.getListingId());

        if(listing.getCrop()!=null)
        {
            dto.setCropName(listing.getCrop().getCropName());
        }
        dto.setVariety(listing.getVariety());
        dto.setGrade(listing.getGrade());
        dto.setHarvestDate(listing.getHarvestDate());
        dto.setQuantity(listing.getQuantity());
        if(listing.getUnit()!= null)
        {
            dto.setUnit(listing.getUnit().getUnitName());
        }

        // Pricing
        dto.setPricePerKg(listing.getPricePerKg());
        dto.setTotalBasePrice(listing.getTotalBasePrice());
        dto.setMinimumBidIncrement(listing.getMinimumBidIncrement());
        dto.setMaximumBidIncrement(listing.getMaximumBidIncrement());
        dto.setPurchaseType(listing.getPurchaseType());
        dto.setSaleType(listing.getSaleType() != null
                ? listing.getSaleType()
                : null);
        dto.setAuctionEndTime(listing.getAuctionEndTime());

        // Partial Order
        dto.setMinimumOrderQuantity(listing.getMinimumOrderQuantity());
        dto.setMoqPricePerKg(listing.getMoqPricePerKg());
        if (listing.getState() != null) {
            dto.setState(listing.getState().getName());
        }

        if (listing.getDistrict() != null) {
            dto.setDistrict(listing.getDistrict().getName());
        }
        if(listing.getPackaging()!=null){
            dto.setPackagingType(listing.getPackaging().getPackagingType());
        }
        if(listing.getStorage()!=null){
            dto.setStorageType(listing.getStorage().getStorageType());
        }
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
        dto.setAuctionStatus(listing.getStatus());
        dto.setHighestBid(listing.getTopBid());

        if (highesBid!=null){
            dto.setHighestBid(highesBid.getBuyerAmount());
            dto.setTopBidderName(highesBid.getBuyer().getFullName());
            dto.setTopBid(highesBid.getBidId());
        }else {
            dto.setHighestBid(null);
            dto.setTopBidderName(null);
            dto.setTopBid(null);
        }

        return dto;
    }

    public static ListingFixedResponseDto toFixedResponseDto(Listing listing,Long topBid) {

        if (listing == null) {
            return null;
        }

        ListingFixedResponseDto dto = new ListingFixedResponseDto();

        dto.setListingId(listing.getListingId());
        dto.setCropName(listing.getCrop().getCropName());
        dto.setVariety(listing.getVariety());

        dto.setQuantity(listing.getQuantity());
        dto.setPricePerKg(listing.getPricePerKg());
        dto.setUnitId(listing.getUnit().getUnitName());

        dto.setTotalBasePrice(listing.getTotalBasePrice());

        dto.setStatus(listing.getStatus().name());
        dto.setPurchaseType(listing.getPurchaseType().name());
        dto.setSaleType(listing.getSaleType().name());

        dto.setPostedOn(listing.getPostedOn());

        dto.setState(listing.getState().getName());
        dto.setDistrict(listing.getDistrict().getName());

        dto.setImages(mapImages(listing));
        dto.setTopBid(topBid);

        return dto;
    }

    public static Listing toEntity(
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto,
            CropMaster crop,
            UnitMaster unit,
            StorageMaster storage,
            PackagingMaster packaging,
            StateMaster state,
            DistrictMaster district,
            List<ListingImage> images,
            ListingCertificate certificate,
            String description) {

        Listing listing = new Listing();

        // Product
        listing.setCrop(crop);
        listing.setVariety(productDto.getVariety());
        listing.setGrade(productDto.getGrade());
        listing.setHarvestDate(productDto.getHarvestDate());

        // Pricing
        listing.setQuantity(pricingDto.getQuantity());
        listing.setUnit(unit);
        listing.setPricePerKg(pricingDto.getPricePerKg());
        listing.setTotalBasePrice(pricingDto.getTotalBasePrice());
        listing.setPurchaseType(pricingDto.getPurchaseType());
        listing.setMinimumBidIncrement(pricingDto.getMinimumBidIncrement());
        listing.setMaximumBidIncrement(pricingDto.getMaximumBidIncrement());
        listing.setSaleType(pricingDto.getSaleType());
        listing.setAuctionEndTime(pricingDto.getAuctionEndTime());
        // Partial Order
        listing.setMinimumOrderQuantity(pricingDto.getMinimumOrderQuantity());
        listing.setMoqPricePerKg(pricingDto.getMoqPricePerKg());

        // Location
        listing.setState(state);
        listing.setDistrict(district);
        listing.setPackaging(packaging);
        listing.setStorage(storage);
        listing.setPickupMethod(locationDto.getPickupMethod());

        // Images (already mapped in service)
        listing.setImages(images);

        // Certificate (already created in service)
        listing.setCertificate(certificate);

        listing.setDescription(description);

        return listing;
    }

    // UPDATE EXISTING ENTITY

    public static void updateEntity(
            Listing listing,
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto,
            CropMaster crop,
            UnitMaster unit,
            StorageMaster storage,
            PackagingMaster packaging,
            StateMaster state,
            DistrictMaster district,
            String description) {

        listing.setCrop(crop);
        listing.setVariety(productDto.getVariety());
        listing.setGrade(productDto.getGrade());
        listing.setHarvestDate(productDto.getHarvestDate());

        listing.setQuantity(pricingDto.getQuantity());
        listing.setUnit(unit);
        listing.setPurchaseType(pricingDto.getPurchaseType());
        listing.setMinimumOrderQuantity(pricingDto.getMinimumOrderQuantity());
        listing.setMoqPricePerKg(pricingDto.getMoqPricePerKg());
        listing.setPricePerKg(pricingDto.getPricePerKg());
        listing.setTotalBasePrice(pricingDto.getTotalBasePrice());
        listing.setMinimumBidIncrement(pricingDto.getMinimumBidIncrement());
        listing.setMaximumBidIncrement(pricingDto.getMaximumBidIncrement());
        listing.setAuctionEndTime(pricingDto.getAuctionEndTime());
        listing.setSaleType(pricingDto.getSaleType());
        listing.setState(state);
        listing.setDistrict(district);
        listing.setPackaging(packaging);
        listing.setStorage(storage);
        listing.setPickupMethod(locationDto.getPickupMethod());

        listing.setDescription(description);
    }

    public static ListingSummaryResponseDto toSummaryResponse(Listing listing) {

        ListingSummaryResponseDto dto = new ListingSummaryResponseDto();
        dto.setListingId(listing.getListingId());

        if (listing.getCrop() != null) {
            dto.setCropId(listing.getCrop().getCropName());
        }
        dto.setVariety(listing.getVariety());
        dto.setGrade(listing.getGrade());
        dto.setQuantity(listing.getQuantity());
        if (listing.getUnit() != null) {
            dto.setUnitId(listing.getUnit().getUnitName());
        }

        dto.setPricePerKg(listing.getPricePerKg());
        dto.setTotalBasePrice(listing.getTotalBasePrice());
        dto.setPurchaseType(listing.getPurchaseType());
        dto.setSaleType(listing.getSaleType() != null
                ? listing.getSaleType().name()
                : null);
        dto.setAuctionEndTime(listing.getAuctionEndTime());

        if (listing.getState() != null) {
            dto.setStateId(listing.getState().getName());
        }
        if (listing.getDistrict() != null) {
            dto.setDistrictId(listing.getDistrict().getName());
        }
        if (listing.getSeller() != null) {
            dto.setSellerName(listing.getSeller().getFullName());
        }

        dto.setImages(mapImages(listing));

        return dto;
    }

    public static List<ProductImageResponseDto> mapImages(Listing listing) {
        if (listing.getImages() == null || listing.getImages().isEmpty()) {
            return null;
        }

        return listing.getImages().stream()
                .filter(ListingImage::getIsPrimary)
                .findFirst()
                .map(img -> {
                    ProductImageResponseDto dto = new ProductImageResponseDto();
                    dto.setFileName(img.getFileName());
                    dto.setFilePath(img.getFilePath());
                    dto.setFileType(img.getFileType());
                    dto.setIsPrimary(img.getIsPrimary());
                    return List.of(dto);
                })
                .orElse(null);
    }
    public static AuctionListingResponseDto toAuctionListingResponseDto(Listing listing, Long topBid) {

        if (listing == null) {
            return null;
        }

        AuctionListingResponseDto dto = new AuctionListingResponseDto();

        // Basic Info
        dto.setListingId(listing.getListingId());
        dto.setCropName(listing.getCrop().getCropName());
        dto.setVariety(listing.getVariety());
        dto.setQuantity(listing.getQuantity());
        dto.setUnit(listing.getUnit().getUnitName());

        // Pricing
        dto.setPricePerKg(listing.getPricePerKg());
        dto.setTotalBasePrice(listing.getTotalBasePrice());
        dto.setPurchaseType(listing.getPurchaseType());
        dto.setSaleType(listing.getSaleType().name());

        // Partial Order fields
        dto.setMinimumOrderQuantity(listing.getMinimumOrderQuantity());

        // Auction fields
        dto.setMinimumBidIncrement(listing.getMinimumBidIncrement());
        dto.setMaximumBidIncrement(listing.getMaximumBidIncrement());
        dto.setAuctionEndTime(listing.getAuctionEndTime());

        // Location
        dto.setState(listing.getState().getName());
        dto.setDistrict(listing.getDistrict().getName());

        // Images
        dto.setImages(mapImages(listing));

        // Description
        dto.setDescription(listing.getDescription());

        // Meta
        dto.setCreatedAt(listing.getCreatedAt());
        dto.setAuctionStatus(listing.getStatus());
        dto.setTopBid(topBid);

        return dto;
    }
}
