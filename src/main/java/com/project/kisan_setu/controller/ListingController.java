package com.project.kisan_setu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.kisan_setu.dto.CreateListingRequest;
import com.project.kisan_setu.dto.DashboardDto;
import com.project.kisan_setu.dto.ListingResponseDto;
import com.project.kisan_setu.dto.SellerListingDto;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.service.ListingService;
import com.project.kisan_setu.service.UserService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;
    private final UserService userService;
    private final ValidatorMethods validatorMethods;
    private static final Logger logger = LoggerFactory.getLogger(ListingController.class);


    @Autowired
    private ObjectMapper objectMapper;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ListingResponseDto> createListing(
            @RequestPart("data") String requestJson,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestPart(value = "certificateFile", required = false) MultipartFile certificateFile
    ) throws JsonProcessingException {

        CreateListingRequest request = objectMapper.readValue(requestJson, CreateListingRequest.class);
        String seller = validatorMethods.getCurrentUserEmail();

        return ResponseEntity.ok(listingService.createListing(request, imageFiles, certificateFile));
    }

    @PutMapping(value = "/{listingId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ListingResponseDto> updateListing(
            @PathVariable Long listingId,
            @RequestPart("data") CreateListingRequest request,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestPart(value = "certificateFile", required = false) MultipartFile certificateFile
    ) {
        return ResponseEntity.ok(listingService.updateListing(listingId, request, imageFiles, certificateFile));
    }

    @GetMapping
    public ResponseEntity<List<ListingResponseDto>> getAllListings() {
        return ResponseEntity.ok(listingService.getAllListings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponseDto> getListingById(@PathVariable Long id) {
        return ResponseEntity.ok(listingService.getListingById(id));
    }

    @DeleteMapping("/{listingId}")
    public ResponseEntity<String> deleteListing(@PathVariable Long listingId,@RequestParam Long sellerId) {
        listingService.deleteListing(listingId,sellerId);
        return ResponseEntity.ok("Listing Deleted Successfully");

    }

//    @PostMapping("/preview")
//    public ResponseEntity<ListingResponseDto> previewListing(
//            @Valid @RequestBody CreateListingRequest request) {
//
//        ListingResponseDto response =
//                listingService.previewListing(request);
//
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/{listingId}/top-5")
    public ResponseEntity<SellerListingDto> getListingDetail(
            @PathVariable Long listingId) {
        String seller = validatorMethods.getCurrentUserEmail();
        return ResponseEntity.ok(listingService.getSellerListingDetail(listingId));
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

    @PutMapping("/seller/{listingId}/mark-sold")
    public ResponseEntity<String> markAsSold(
            @PathVariable Long listingId,
            @RequestParam Long sellerId) {
        listingService.markAsSold(listingId, sellerId);
        return ResponseEntity.ok("Listing marked as SOLD successfully");
    }

    @PutMapping("/{listingId},extends")
    public ResponseEntity<String> extendAuctionTime(@PathVariable Long listingId,@RequestParam Long sellerId,@RequestParam int minutes){
        listingService.extendAuctionTime(listingId,sellerId,minutes);
        return ResponseEntity.ok("Auction time extended successfully");

    }
}
