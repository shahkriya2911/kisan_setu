package com.project.kisan_setu.service.impl;

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
    private final String uploadDir = "uploads/";
    private final ProductImageRepository productImageRepository;
    private final QualityCertificateRepository qualityCertificateRepository;


    public ListingServiceImpl(ListingRepository listingRepository, ProductImageRepository productImageRepository, QualityCertificateRepository qualityCertificateRepository) {
        this.listingRepository = listingRepository;
        this.productImageRepository = productImageRepository;
        this.qualityCertificateRepository = qualityCertificateRepository;
    }

    @Override
    public Listing createListing(ProductListingDto productDto, QualityPricingListingDto pricingDto, QualityLocationListingDto locationDto) {

        Listing listing = ListingMapper.toEntity(productDto, pricingDto, locationDto);

        return listingRepository.save(listing);
    }

    @Override
    public List<Listing> getAllListings() {
        return listingRepository.findAll();
    }

    @Override
    public Listing getListingById(Long id) {
        return listingRepository.findById(id).orElseThrow(()-> new UserException("Listing not Found with id: " +id));
    }

    public void deleteListing(Long id){
        Listing listing=getListingById(id);
        listingRepository.delete(listing);
    }

    @Override
    public Listing previewListing(ProductListingDto productDto, QualityPricingListingDto pricingDto, QualityLocationListingDto locationDto) {
        // Just convert DTO → Entity
        // DO NOT save to DB
        return ListingMapper.toEntity(productDto,pricingDto,locationDto);
    }




}
