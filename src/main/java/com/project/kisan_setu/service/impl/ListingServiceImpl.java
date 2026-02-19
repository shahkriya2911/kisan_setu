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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private static final Logger logger= LoggerFactory.getLogger(ListingServiceImpl.class);

    public ListingServiceImpl(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Override
    public ListingResponseDto createListing(
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto) {
        logger.info("Creating new listing for product: {}", productDto.getCropName());

        Listing listing = ListingMapper.toEntity(
                productDto,
                pricingDto,
                locationDto
        );

        Listing saved = listingRepository.save(listing);
        logger.info("Listing created successfully with ID: {}", saved.getListingId());


        return ListingMapper.toResponse(saved);
    }

    @Override
    public List<ListingResponseDto> getAllListings() {
        logger.info("Fetching all listings");

        return listingRepository.findAll()
                .stream()
                .map(ListingMapper::toResponse)
                .toList();

    }

    @Override
    public ListingResponseDto getListingById(Long id) {
        logger.info("Fetching listing with ID: {}", id);

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() ->{
                    logger.error("Listing not found with ID: {}", id);
                        return new UserException("Listing not found with id: " + id);});

        return ListingMapper.toResponse(listing);
    }

    @Override
    public void deleteListing(Long id) {
        logger.info("Deleting listing with ID: {}", id);

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() ->{logger.error("Can not Delete it.User not found with id: {}",id);
                        return new UserException("Listing not found with id: " + id);
                });

        listingRepository.delete(listing);
    }

    @Override
    public ListingResponseDto previewListing(
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto) {
        logger.debug("Previewing listing for product: {}", productDto.getCropName());

        Listing preview = ListingMapper.toEntity(
                productDto,
                pricingDto,
                locationDto
        );

        return ListingMapper.toResponse(preview);
    }
}
