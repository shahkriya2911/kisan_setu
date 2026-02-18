package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.CreateListingRequest;
import com.project.kisan_setu.dto.ListingResponseDto;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.service.ListingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/listings")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @PostMapping
    public ResponseEntity<ListingResponseDto> createListing(
            @RequestBody CreateListingRequest request) {

        ListingResponseDto savedListing =
                listingService.createListing(
                        request.getProduct(),
                        request.getPricing(),
                        request.getLocation()
                );

        return ResponseEntity.ok(savedListing);
    }

    @GetMapping
    public ResponseEntity<List<ListingResponseDto>> getAllListings() {
        return ResponseEntity.ok(listingService.getAllListings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponseDto> getListingById(
            @PathVariable Long id) {

        return ResponseEntity.ok(listingService.getListingById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteListing(@PathVariable Long id) {
        listingService.deleteListing(id);
        return ResponseEntity.ok("Listing Deleted Successfully");
    }

    @PostMapping("/preview")
    public ResponseEntity<ListingResponseDto> previewListing(
            @RequestBody CreateListingRequest request) {

        ListingResponseDto preview =
                listingService.previewListing(
                        request.getProduct(),
                        request.getPricing(),
                        request.getLocation()
                );

        return ResponseEntity.ok(preview);
    }
}
