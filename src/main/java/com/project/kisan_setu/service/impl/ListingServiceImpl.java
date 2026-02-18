package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.ListingResponseDto;
import com.project.kisan_setu.dto.ProductListingDto;
import com.project.kisan_setu.dto.QualityLocationListingDto;
import com.project.kisan_setu.dto.QualityPricingListingDto;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.ListingMapper;
import com.project.kisan_setu.repository.ProductImageRepository;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.repository.QualityCertificateRepository;
import com.project.kisan_setu.service.ListingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;

    public ListingServiceImpl(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Override
    public ListingResponseDto createListing(
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto) {

        Listing listing = ListingMapper.toEntity(
                productDto,
                pricingDto,
                locationDto
        );

        Listing saved = listingRepository.save(listing);

        return ListingMapper.toResponse(saved);
    }

    @Override
    public List<ListingResponseDto> getAllListings() {

        return listingRepository.findAll()
                .stream()
                .map(ListingMapper::toResponse)
                .toList();
    }

    @Override
    public ListingResponseDto getListingById(Long id) {

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() ->
                        new UserException("Listing not found with id: " + id));

        return ListingMapper.toResponse(listing);
    }

    @Override
    public void deleteListing(Long id) {

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() ->
                        new UserException("Listing not found with id: " + id));

        listingRepository.delete(listing);
    }

    @Override
    public ListingResponseDto previewListing(
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto) {

        Listing preview = ListingMapper.toEntity(
                productDto,
                pricingDto,
                locationDto
        );

        return ListingMapper.toResponse(preview);
    }
}
