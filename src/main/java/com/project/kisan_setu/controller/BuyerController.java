package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.RequestDto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.ResponseDto.BuyerListingResponseDto;
import com.project.kisan_setu.dto.ResponseDto.BuyingRequirementResponseDto;
import com.project.kisan_setu.dto.RequestDto.PlaceBidRequestDto;
import com.project.kisan_setu.dto.ResponseDto.ListingSummaryResponseDto;
import com.project.kisan_setu.dto.ResponseDto.MyBiddingsResponseDto;
import com.project.kisan_setu.service.BidService;
import com.project.kisan_setu.service.BuyerService;
import com.project.kisan_setu.util.ValidatorMethods;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/buyers")
@RequiredArgsConstructor
public class BuyerController {

    //constructor dependency injection
    private final BuyerService buyerService;
    private final BidService bidService;
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
    public Page<BuyerListingResponseDto> getAuctionListings(Pageable pageable) {

        Long userId = validatorMethods.getCurrentUserId();

        return buyerService.getActiveAuctionListings(userId, pageable);
    }

    //get all fixed listings
    @GetMapping("/fixed")
    public Page<BuyerListingResponseDto> getFixedListings(Pageable pageable
    ) {
        Long userId = validatorMethods.getCurrentUserId();

        return buyerService.getActiveFixedListings(userId, pageable);
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

    @GetMapping("/my-all-bids")
    public ResponseEntity<List<MyBiddingsResponseDto>> getAllMyBids(){
        return ResponseEntity.ok(bidService.getAllMyBids());
    }

    @GetMapping("my-pending-bids")
    public ResponseEntity<List<MyBiddingsResponseDto>> getMyPendingBids(){
        return ResponseEntity.ok(bidService.getMyPendingBids());
    }

    @GetMapping("my-accepted-bids")
    public ResponseEntity<List<MyBiddingsResponseDto>> getMyAcceptedBids(){
        return ResponseEntity.ok(bidService.getMyAcceptedBids());
    }

    @GetMapping("my-rejected-bids")
    public ResponseEntity<List<MyBiddingsResponseDto>> getMyRejectedBids(){
        return ResponseEntity.ok(bidService.getMyRejectedBids());
    }

    @GetMapping("my-outbid-bids")
    public ResponseEntity<List<MyBiddingsResponseDto>> getMyOutbidBids(){
        List<MyBiddingsResponseDto> response = bidService.getMyOutbidBids();

        return ResponseEntity.ok(response);
    }

}
