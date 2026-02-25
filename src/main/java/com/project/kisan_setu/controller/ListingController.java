package com.project.kisan_setu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.service.ListingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/listings")
public class ListingController {

    private final ListingService listingService;
    private static final Logger logger = LoggerFactory.getLogger(ListingController.class);

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }
    @Autowired
    private ObjectMapper objectMapper;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ListingResponseDto> createListing(
            @RequestPart("data") String requestJson,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestPart(value = "certificateFile", required = false) MultipartFile certificateFile
    ) throws JsonProcessingException {

        // uses the injected mapper with JavaTimeModule
        CreateListingRequest request = objectMapper.readValue(requestJson, CreateListingRequest.class);

        return ResponseEntity.ok(listingService.createListing(request, imageFiles, certificateFile));
    }


    //  UPDATE
    @PutMapping(value = "/{listingId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ListingResponseDto> updateListing(
            @PathVariable Long listingId,
            @RequestPart("data") CreateListingRequest request,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestPart(value = "certificateFile", required = false) MultipartFile certificateFile
    ) {
        return ResponseEntity.ok(listingService.updateListing(listingId, request, imageFiles, certificateFile));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<ListingResponseDto>> getAllListings() {
        return ResponseEntity.ok(listingService.getAllListings());
    }

    //  GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ListingResponseDto> getListingById(@PathVariable Long id) {
        return ResponseEntity.ok(listingService.getListingById(id));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteListing(@PathVariable Long id) {
        listingService.deleteListing(id);
        return ResponseEntity.ok("Listing Deleted Successfully");
    }

    //  PREVIEW
    @PostMapping("/preview")
    public ResponseEntity<ListingResponseDto> previewListing(@RequestBody CreateListingRequest request) {
        return ResponseEntity.ok(listingService.previewListing(
                request.getProduct(),
                request.getPricing(),
                request.getLocation()
        ));
    }

    //  TOP 5 BIDS
    @GetMapping("/{listingId}/top-5")
    public ResponseEntity<SellerListingDto> getListingDetail(@PathVariable Long listingId) {
        return ResponseEntity.ok(listingService.getListingTop5BidDetail(listingId));
    }

    // DASHBOARD
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getSellerOverView() {
        return ResponseEntity.ok(listingService.getSellerOverview());
    }
}