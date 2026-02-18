package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.CreateListingRequest;
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
    public ResponseEntity<Listing> createListing(@RequestBody CreateListingRequest request)
    {
        // 👇 PUT DEBUG HERE
        System.out.println(request);
        System.out.println(request.getProduct());
        Listing savedListing = listingService.createListing(request.getProduct(),
                request.getPricing(),
                request.getLocation());

        return ResponseEntity.ok(savedListing);
    }

    @GetMapping
    public ResponseEntity<List<Listing>> getAllListings() {

        return ResponseEntity.ok(listingService.getAllListings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Listing> getListingById(@PathVariable Long id){
        return ResponseEntity.ok(listingService.getListingById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> getListingDeleteById(@PathVariable Long id){
        listingService.deleteListing(id);
        return ResponseEntity.ok("Listing Deleted Successfully");
    }

    @PostMapping("/preview")
    public ResponseEntity<Listing> getListingPreview(@RequestBody CreateListingRequest request)
    {
        Listing preview = listingService.previewListing(request.getProduct(),
                request.getPricing(),
                request.getLocation());
        return ResponseEntity.ok(preview);
    }

}
