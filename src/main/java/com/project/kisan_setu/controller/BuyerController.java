package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.RequestDto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.ResponseDto.BuyingRequirementResponseDto;
import com.project.kisan_setu.dto.RequestDto.PlaceBidRequestDto;
import com.project.kisan_setu.dto.ResponseDto.ListingSummaryResponseDto;
import com.project.kisan_setu.service.BuyerService;
import com.project.kisan_setu.util.ValidatorMethods;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/buyers")
@RequiredArgsConstructor
public class BuyerController {

    //constructor dependency injection
    private final BuyerService buyerService;
    private final ValidatorMethods validatorMethods;
    private static final Logger logger = LoggerFactory.getLogger(BuyerController.class);

    @PostMapping("/buyer-requirement")
    public BuyingRequirementResponseDto createRequirement(
            @Valid @RequestBody BuyingRequirementRequestDto dto) {
        logger.debug("Create buyer requirement request attempt for buyer");
        logger.info("Buyer requirement created successfully");
        return buyerService.postRequirement(dto);
    }


    //get all auction listings
    @GetMapping("/auctions")
    public ResponseEntity<?> getAuctionListings() {
        logger.info("Get all auction listings request attempt");
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        logger.info("Fetched all auction listings successfully");
        return ResponseEntity.ok(
                buyerService.getActiveAuctionListings(userId));
    }

    //get all fixed listings
    @GetMapping("/fixed")
    public ResponseEntity<?> getFixedListings() {
        logger.info("Get all fixed listings request attempt");
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        logger.info("Fetched all fixed listings successfully");
        return ResponseEntity.ok(
                buyerService.getActiveFixedListings(userId));
    }

    @GetMapping("/auctions/{listingId}")
    public ResponseEntity<?> getAuctionListingDetail(@PathVariable Long listingId) {
        logger.debug("Get auction listing detail request attempt for listing id : {}",listingId);
        logger.info("Fetched auction listing detail successfully");
        return ResponseEntity.ok(buyerService.getAuctionListingDetail(listingId));
    }

    @GetMapping("/fixed/{listingId}")
    public ResponseEntity<?> getFixedListingDetail(@PathVariable("listingId") Long listingId){
        logger.debug("Get fixed listing detail request attempt for listing id : {}",listingId);
        logger.info("Fetched fixed listing detail successfully");
        return ResponseEntity.ok(buyerService.getFixedListingDetail(listingId));
    }


    //post a bid in a particular auction or fixed listing
    @PostMapping("/{listingId}/auctions")
    public ResponseEntity<?> placeAction(
            @PathVariable Long listingId,
            @RequestBody PlaceBidRequestDto dto) {
        logger.debug("Post bid for listing with id : {}",listingId);

        Object response =
                buyerService.placeBid(listingId, dto);
        logger.info("Bid placed successfully");
        return ResponseEntity.ok(response);

    }

    //git bid history of a particular auction or fixed listing
    @GetMapping("/auctions/{listingId}/bids")
    public ResponseEntity<?> getBidHistory(
            @PathVariable Long listingId) {
        logger.debug("Get bid history for listing with id : {} request attempt",listingId);
        logger.info("Fetched bid history successfully");
        return ResponseEntity.ok(
                buyerService.getBidHistory(listingId)
        );
    }

    @GetMapping("/listings/{listingId}/summary")
    public ResponseEntity<ListingSummaryResponseDto> getListingSummary(
            @PathVariable Long listingId) {
        logger.debug("Get listing summary for listing with id : {}", listingId);
        logger.info("Listing summary fetched successfully");
        return ResponseEntity.ok(buyerService.getListingSummary(listingId));
    }
}
