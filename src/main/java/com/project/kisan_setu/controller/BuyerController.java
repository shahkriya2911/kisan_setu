package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.PlaceBidRequestDto;
import com.project.kisan_setu.service.BuyerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController //handling of rest apis
@RequestMapping("/buyers") //api starts with /buyers
public class BuyerController {

    //constructor dependency injection
    private final BuyerService buyerService;
    private static final Logger logger = LoggerFactory.getLogger(BuyerController.class);
    public BuyerController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    //create buying requirement
    @PostMapping("/requirements")
    public ResponseEntity<?> postRequirement(
            @RequestParam Long buyerId,
            @RequestBody BuyingRequirementRequestDto dto) { //json from user
        logger.debug("Create buying requirement request for user with id : {}",buyerId);

        return ResponseEntity.ok(
                buyerService.postRequirement(buyerId, dto)
        );
    }

    //get all auction listings
    @GetMapping("/auctions")
    public ResponseEntity<?> getAuctionListings() {

        return ResponseEntity.ok(
                buyerService.getActiveAuctionListings()
        );
    }

    //post a bid in a particular auction listing
    @PostMapping("/auctions/{listingId}/bid")
    public ResponseEntity<?> placeBid(
            @RequestParam Long buyerId,
            @PathVariable Long listingId,
            @RequestBody PlaceBidRequestDto dto) {

        return ResponseEntity.ok(
                buyerService.placeBid(buyerId, listingId, dto)
        );
    }

    //git bid history of a particular auction
    @GetMapping("/auctions/{listingId}/bids")
    public ResponseEntity<?> getBidHistory(
            @PathVariable Long listingId) {

        return ResponseEntity.ok(
                buyerService.getBidHistory(listingId)
        );
    }
}
