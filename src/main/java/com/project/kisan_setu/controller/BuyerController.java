package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.RequestDto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.ResponseDto.BuyerListingResponseDto;
import com.project.kisan_setu.dto.ResponseDto.BuyingRequirementResponseDto;
import com.project.kisan_setu.dto.RequestDto.PlaceBidRequestDto;
import com.project.kisan_setu.dto.ResponseDto.MyBiddingsResponseDto;
import com.project.kisan_setu.service.BidService;
import com.project.kisan_setu.service.BuyerService;
import com.project.kisan_setu.util.ValidatorMethods;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/buyers")
@RequiredArgsConstructor
@Tag(name = "Buyer Management", description = "Endpoints for buyer related resources")
public class BuyerController {

    //constructor dependency injection
    private final BuyerService buyerService;
    private final BidService bidService;
    private final ValidatorMethods validatorMethods;
    private static final Logger logger = LoggerFactory.getLogger(BuyerController.class);

    @PostMapping("/buyer-requirement")
    @Operation(summary = "Create buyer requirement method", description = "Used by buyer to create a buying requirement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Buyer requirement created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid buyer requirement details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public BuyingRequirementResponseDto createRequirement(
            @Parameter(description = "Buyer requirement details", required = true)
            @Valid @RequestBody BuyingRequirementRequestDto dto) {
        logger.debug("Create buyer requirement request attempt for buyer");
        logger.info("Buyer requirement created successfully");
        return buyerService.postRequirement(dto);
    }


    //get all auction listings
    @GetMapping("/auctions")
    @Operation(summary = "Get auction listings method", description = "Used by buyer to get all active auction listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Auction listings fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public Page<BuyerListingResponseDto> getAuctionListings(Pageable pageable) {

        Long userId = validatorMethods.getCurrentUserId();

        return buyerService.getActiveAuctionListings(userId, pageable);
    }

    //get all fixed listings
    @GetMapping("/fixed")
    @Operation(summary = "Get fixed listings method", description = "Used by buyer to get all active fixed listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fixed listings fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public Page<BuyerListingResponseDto> getFixedListings(Pageable pageable
    ) {
        Long userId = validatorMethods.getCurrentUserId();

        return buyerService.getActiveFixedListings(userId, pageable);
    }

    @GetMapping("/auctions/{listingId}")
    @Operation(summary = "Get auction listing detail method", description = "Used by buyer to get auction listing detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Auction listing detail fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Listing not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<BuyerListingResponseDto> getAuctionListingDetail(
            @Parameter(description = "Listing ID request", required = true)
            @PathVariable Long listingId) {
        logger.debug("Get auction listing detail request attempt for listing id : {}",listingId);
        logger.info("Fetched auction listing detail successfully");
        return ResponseEntity.ok(buyerService.getAuctionListingDetail(listingId));
    }

    @GetMapping("/fixed/{listingId}")
    @Operation(summary = "Get fixed listing detail method", description = "Used by buyer to get fixed listing detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fixed listing detail fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Listing not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<BuyerListingResponseDto> getFixedListingDetail(
            @Parameter(description = "Listing ID request", required = true)
            @PathVariable("listingId") Long listingId){
        logger.debug("Get fixed listing detail request attempt for listing id : {}",listingId);
        logger.info("Fetched fixed listing detail successfully");
        return ResponseEntity.ok(buyerService.getFixedListingDetail(listingId));
    }


    //post a bid in a particular auction or fixed listing
    @PostMapping("/{listingId}/auctions")
    @Operation(summary = "Place bid method", description = "Used by buyer to place a bid on a listing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bid placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid bid details"),
            @ApiResponse(responseCode = "404", description = "Listing not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<Object> placeAction(
            @Parameter(description = "Listing ID request", required = true)
            @PathVariable Long listingId,
            @Parameter(description = "Bid details", required = true)
            @RequestBody PlaceBidRequestDto dto) {
        logger.debug("Post bid for listing with id : {}",listingId);

        Object response =
                buyerService.placeBid(listingId, dto);
        logger.info("Bid placed successfully");
        return ResponseEntity.ok(response);

    }

    @GetMapping("/my-all-bids")
    @Operation(summary = "Get all my bids method", description = "Used by buyer to get all of their bids")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All bids fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<MyBiddingsResponseDto>> getAllMyBids(){
        return ResponseEntity.ok(bidService.getAllMyBids());
    }

    @GetMapping("my-pending-bids")
    @Operation(summary = "Get pending bids method", description = "Used by buyer to get pending bids")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending bids fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<MyBiddingsResponseDto>> getMyPendingBids(){
        return ResponseEntity.ok(bidService.getMyPendingBids());
    }

    @GetMapping("my-accepted-bids")
    @Operation(summary = "Get accepted bids method", description = "Used by buyer to get accepted bids")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accepted bids fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<MyBiddingsResponseDto>> getMyAcceptedBids(){
        return ResponseEntity.ok(bidService.getMyAcceptedBids());
    }

    @GetMapping("my-rejected-bids")
    @Operation(summary = "Get rejected bids method", description = "Used by buyer to get rejected bids")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rejected bids fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<MyBiddingsResponseDto>> getMyRejectedBids(){
        return ResponseEntity.ok(bidService.getMyRejectedBids());
    }

    @GetMapping("my-outbid-bids")
    @Operation(summary = "Get outbid bids method", description = "Used by buyer to get outbid bids")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Outbid bids fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<MyBiddingsResponseDto>> getMyOutbidBids(){
        List<MyBiddingsResponseDto> response = bidService.getMyOutbidBids();

        return ResponseEntity.ok(response);
    }

}
