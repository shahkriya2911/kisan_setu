package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.CreateListingRequest;
import com.project.kisan_setu.dto.DashboardDto;
import com.project.kisan_setu.dto.ListingResponseDto;
import com.project.kisan_setu.dto.SellerListingDto;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.service.ListingService;
import com.project.kisan_setu.service.impl.ListingServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/listings")
public class ListingController {

    private final ListingService listingService;
    private static final Logger logger = LoggerFactory.getLogger(ListingController.class);

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @PostMapping
    public ResponseEntity<ListingResponseDto> createListing(
            @Valid @RequestBody CreateListingRequest request) {

        ListingResponseDto response =
                listingService.createListing(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ListingResponseDto>> getAllListings() {
        logger.info("Fetching all listings");
        return ResponseEntity.ok(listingService.getAllListings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponseDto> getListingById(
            @PathVariable Long id) {
        logger.debug("Fetching listing with ID: {}", id);
        ListingResponseDto listing = listingService.getListingById(id);
        logger.info("Listing fetched successfully with ID: {}", id);
        return ResponseEntity.ok(listing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteListing(@PathVariable Long id) {
        logger.warn("Request received to delete listing with ID: {}", id);
        listingService.deleteListing(id);
        logger.warn("Listing deleted successfully with ID: {}", id);
        return ResponseEntity.ok("Listing Deleted Successfully");
    }

    @PostMapping("/preview")
    public ResponseEntity<ListingResponseDto> previewListing(
            @Valid @RequestBody CreateListingRequest request) {

        ListingResponseDto response =
                listingService.previewListing(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{listingId}/top-5")
    public ResponseEntity<SellerListingDto> getListingDetail(
            @PathVariable Long listingId) {
        return ResponseEntity.ok(listingService.getListingTop5BidDetail(listingId));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getSellerOverView(){
        return ResponseEntity.ok(listingService.getSellerOverview());
    }

    @PutMapping("{listingId}")
    public ResponseEntity<ListingResponseDto> updateCrop(@PathVariable Long listingId,
                                                         @RequestBody CreateListingRequest request)
    {
        ListingResponseDto response =
                listingService.updateListing(
                        listingId,
                        request.getProduct(),
                        request.getPricing(),
                        request.getLocation()
                );
        return ResponseEntity.ok(response);


    }
}
