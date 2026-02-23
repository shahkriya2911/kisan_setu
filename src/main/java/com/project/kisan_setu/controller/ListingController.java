package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.CreateListingRequest;
import com.project.kisan_setu.dto.ListingResponseDto;
import com.project.kisan_setu.dto.SellerListingDto;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.service.ListingService;
import com.project.kisan_setu.service.impl.ListingServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/listings")
public class ListingController {

    private final ListingService listingService;
    private static final Logger logger= LoggerFactory.getLogger(ListingController.class);

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @PostMapping
    public ResponseEntity<ListingResponseDto> createListing(
            @RequestBody CreateListingRequest request) {
        logger.info("Received request to create listing for product: {}", request.getProduct());

        ListingResponseDto savedListing =
                listingService.createListing(
                        request.getProduct(),
                        request.getPricing(),
                        request.getLocation()
                );
        logger.info("Listing created successfully with ID: {}", savedListing.getListingId());

        return ResponseEntity.ok(savedListing);
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
            @RequestBody CreateListingRequest request) {
        logger.info("Preview request received for product: {}", request.getProduct());

        ListingResponseDto preview =
                listingService.previewListing(
                        request.getProduct(),
                        request.getPricing(),
                        request.getLocation()
                );
        logger.info("Preview generated successfully");

        return ResponseEntity.ok(preview);
    }

<<<<<<< Updated upstream
    @GetMapping("/{listingId}/top-5")
    public ResponseEntity<SellerListingDto> getListingDetail(
            @PathVariable Long listingId) {
        return ResponseEntity.ok(listingService.getListingTop5BidDetail(listingId));
    }

=======
>>>>>>> Stashed changes

}
