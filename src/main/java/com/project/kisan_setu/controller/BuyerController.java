package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.PlaceBidRequestDto;
import com.project.kisan_setu.service.BuyerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/buyers")
public class BuyerController {

    private final BuyerService buyerService;

    public BuyerController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @PostMapping("/requirements")
    public ResponseEntity<?> postRequirement(
            @RequestParam Long buyerId,
            @RequestBody BuyingRequirementRequestDto dto) {

        return ResponseEntity.ok(
                buyerService.postRequirement(buyerId, dto)
        );
    }

    @GetMapping("/auctions")
    public ResponseEntity<?> getAuctionListings() {

        return ResponseEntity.ok(
                buyerService.getActiveAuctionListings()
        );
    }

    @PostMapping("/auctions/{listingId}/bid")
    public ResponseEntity<?> placeBid(
            @RequestParam Long buyerId,
            @PathVariable Long listingId,
            @RequestBody PlaceBidRequestDto dto) {

        return ResponseEntity.ok(
                buyerService.placeBid(buyerId, listingId, dto)
        );
    }

    @GetMapping("/auctions/{listingId}/bids")
    public ResponseEntity<?> getBidHistory(
            @PathVariable Long listingId) {

        return ResponseEntity.ok(
                buyerService.getBidHistory(listingId)
        );
    }
}
