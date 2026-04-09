package com.project.kisan_setu.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.kisan_setu.dto.RequestDto.CreateListingRequest;
import com.project.kisan_setu.dto.RequestDto.ExtendAuctionDto;
import com.project.kisan_setu.dto.ResponseDto.BuyerContactResponseDto;
import com.project.kisan_setu.dto.ResponseDto.BuyingRequirementResponseDto;
import com.project.kisan_setu.dto.ResponseDto.DashboardDto;
import com.project.kisan_setu.dto.ResponseDto.ListingResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ListingSummaryResponseDto;
import com.project.kisan_setu.dto.ResponseDto.RecentBidResponseDto;
import com.project.kisan_setu.dto.ResponseDto.SellerListingDto;
import com.project.kisan_setu.dto.ResponseDto.SellerListingFixedDto;
import com.project.kisan_setu.service.ListingService;
import com.project.kisan_setu.service.UserService;
import com.project.kisan_setu.util.ValidatorMethods;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("api/listings")
@RequiredArgsConstructor
@Tag(name = "Listing Management", description = "Endpoints for listings related resources")
public class ListingController {

    private final ListingService listingService;
    private final UserService userService;
    private final ValidatorMethods validatorMethods;
    private static final Logger logger = LoggerFactory.getLogger(ListingController.class);

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create Listing method", description = "Used by user for creating listing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Listing created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad listing credentials"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<ListingResponseDto> createListing(
            @Parameter(description = "Create listing credentials", required = true) @RequestPart("data") String requestJson,
            @Parameter(description = "Listing image/s", required = false) @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @Parameter(description = "Listing certificate", required = false) @RequestPart(value = "certificateFile", required = false) MultipartFile certificateFile)
            throws JsonProcessingException {
        logger.debug("Create listing request attempt by user");
        CreateListingRequest request = objectMapper.readValue(requestJson, CreateListingRequest.class);
        Long sellerId = validatorMethods.getCurrentUserId();
        logger.info("listing created successfully for user with id : {}", sellerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(listingService.createListing(request, imageFiles, certificateFile));
    }

    @PutMapping(value = "/{listingId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update listing method", description = "Used by user for updating listing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listing updated successfully"),
            @ApiResponse(responseCode = "404", description = "Listing not found"),
            @ApiResponse(responseCode = "400", description = "Bad listing credentials"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<ListingResponseDto> updateListing(
            @Parameter(description = "Listing ID", required = true) @PathVariable Long listingId,
            @Parameter(description = "Listing credentials", required = true) @RequestPart("data") CreateListingRequest request,
            @Parameter(description = "Listing image/s", required = false) @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @Parameter(description = "Listing certificate", required = false) @RequestPart(value = "certificateFile", required = false) MultipartFile certificateFile) {
        logger.debug("Update listing request attempt for listing with id : {}", listingId);
        logger.info("Update listing successful for listing with id : {}", listingId);
        return ResponseEntity.ok(listingService.updateListing(listingId, request, imageFiles, certificateFile));
    }

    @GetMapping
    @Operation(summary = "Get all listings method", description = "Used to get all listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listings fetched successfully"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    public ResponseEntity<List<ListingResponseDto>> getAllListings() {
        logger.info("Get all listings request attempt");
        logger.info("Fetched all listings successfully");
        return ResponseEntity.ok(listingService.getAllListings());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get listing by ID method", description = "Used to get listing by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listing fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Listing not found"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    public ResponseEntity<ListingResponseDto> getListingById(
            @Parameter(description = "Listing ID", required = true) @PathVariable Long id) {
        logger.debug("Get listing by id attempt for listing with id : {}", id);
        logger.info("Listing with id : {} fetched successfully", id);
        return ResponseEntity.ok(listingService.getListingById(id));
    }

    @DeleteMapping("/{listingId}")
    @Operation(summary = "Delete listing method", description = "Used by seller to delete a listing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listing deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Listing not found"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<String> deleteListing(
            @Parameter(description = "Listing ID", required = true) @PathVariable Long listingId,
            @Parameter(description = "Seller ID", required = true) @RequestParam Long sellerId) {
        logger.debug("Delete listing with id request attempt for listing with id : {} by user with id : {}", listingId,
                sellerId);
        listingService.deleteListing(listingId, sellerId);
        logger.info("Delete listing with id : {} successful", listingId);
        return ResponseEntity.ok("Listing Deleted Successfully");

    }

    @GetMapping("/{listingId}/top-5")
    @Operation(summary = "Get top 5 bids for auction listing", description = "Used by seller to get top 5 bids for their auction listing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top 5 bids fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Listing not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<SellerListingDto> getAuctionListingDetail(
            @Parameter(description = "Listing ID", required = true) @PathVariable Long listingId) {
        logger.debug("Get top 5 bids for listing with id : {} request attempt", listingId);
        Long sellerId = validatorMethods.getCurrentUserId();
        logger.info("Fetched top 5 bids successfully for listing with id : {}", listingId);
        return ResponseEntity.ok(listingService.getSellerAuctionListingDetail(listingId));
    }

    @GetMapping("/{listingId}/fixed")
    @Operation(summary = "Get fixed listing detail", description = "Used by seller to get fixed listing detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fixed listing fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Listing not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<SellerListingFixedDto> getFixedListingDetail(
            @Parameter(description = "Listing ID", required = true) @PathVariable Long listingId) {
        logger.debug("Get fixed listing with id : {} request attempt", listingId);
        Long sellerId = validatorMethods.getCurrentUserId();
        logger.info("Fetched fixed listing with id : {}", listingId);
        return ResponseEntity.ok(listingService.getSellerFixedListingDetail(listingId));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get seller dashboard overview", description = "Used by seller to get their dashboard overview")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard overview fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<DashboardDto> getSellerOverView() {
        logger.info("Get seller overview request");
        logger.info("Seller overview fetched successfully");
        return ResponseEntity.ok(listingService.getSellerOverview());
    }


//    @PutMapping("/seller/{listingId}/mark-sold")
//    public ResponseEntity<String> markAsSold(
//            @PathVariable Long listingId,
//            @RequestParam Long sellerId) {
//        logger.debug("Listing marked as sold request attempt for listing with id : {}",listingId);
//        listingService.markAsSold(listingId, sellerId);
//        logger.info("Listing marked as sold successfully");
//        return ResponseEntity.ok("Listing marked as SOLD successfully");
//    }

    @PostMapping("/extend-auction")
    @Operation(summary = "Extend auction time", description = "Used by seller to extend auction time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Auction time extended successfully"),
            @ApiResponse(responseCode = "400", description = "Bad input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<String> extendAuction(
            @Parameter(description = "Extend auction DTO", required = true) @RequestBody ExtendAuctionDto dto) {

        String response = listingService.extendAuctionTime(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-pending")
    @Operation(summary = "Get pending listings", description = "Used by seller to get their pending listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending listings fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<Page<ListingResponseDto>> pendingListings(
            @Parameter(description = "Authentication object", required = true) Authentication authentication,
            @Parameter(description = "Pagination and sorting parameters") Pageable pageable) {
        logger.debug("Get all pending listings for user with id : {}", Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        Page<ListingResponseDto> listings = listingService.pendingListings(userId, pageable);
        logger.info("Fetched all pending listings for user with id : {} successfully", userId);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/my-sold")
    @Operation(summary = "Get sold listings", description = "Used by seller to get their sold listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sold listings fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<Page<ListingResponseDto>> soldListings(
            @Parameter(description = "Authentication object", required = true) Authentication authentication,
            @Parameter(description = "Pagination and sorting parameters") Pageable pageable) {
        logger.debug("Get all sold listings for user with id : {}", Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        Page<ListingResponseDto> listings = listingService.soldListings(userId, pageable);
        logger.info("Fetched all sold listings for user with id : {} successfully", userId);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/my-closed")
    @Operation(summary = "Get closed listings", description = "Used by seller to get their closed listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Closed listings fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<Page<ListingResponseDto>> closedListings(
            @Parameter(description = "Authentication object", required = true) Authentication authentication,
            @Parameter(description = "Pagination and sorting parameters") Pageable pageable) {
        logger.debug("Get all closed listings for user with id : {}", Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        Page<ListingResponseDto> listings = listingService.closedListings(userId, pageable);
        logger.info("Fetched all closed listings for user with id : {} successfully", userId);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/my-active")
    @Operation(summary = "Get active listings", description = "Used by seller to get their active listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active listings fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<Page<ListingResponseDto>> activeListings(
            @Parameter(description = "Authentication object", required = true) Authentication authentication,
            @Parameter(description = "Pagination and sorting parameters") Pageable pageable) {
        logger.debug("Get all active listings for user with id : {}", Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        Page<ListingResponseDto> listings = listingService.activeListings(userId, pageable);
        logger.info("Fetched all active listings for user with id : {} successfully", userId);
        return ResponseEntity.ok(listings);
    }

//    @GetMapping("/my-active")
//    public ResponseEntity<Page<Object>> activeListings(Authentication authentication, Pageable pageable){
//        logger.debug("Get all active listings for user with id : {}",Long.parseLong(authentication.getName()));
//        Long userId = Long.parseLong(authentication.getName());
//        Page<Object> listings = listingService.activeListings(userId,pageable);
//        logger.info("Fetched all active listings for user with id : {} successfully",userId);
//        return ResponseEntity.ok(listings);
//    }

    @GetMapping("my-listings")
    @Operation(summary = "Get all my listings", description = "Used by seller to get all their listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listings fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<Page<ListingResponseDto>> myListings(
            @Parameter(description = "Authentication object", required = true) Authentication authentication,
            @Parameter(description = "Pagination and sorting parameters") @PageableDefault(size = 20, sort = "postedOn") Pageable pageable) {
        logger.debug("Get all my listings for user with id : {}", Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        Page<ListingResponseDto> listings = listingService.myListings(userId, pageable);
        logger.info("Fetched all my listings for user with id : {} successfully", userId);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/seller/requirements")
    @Operation(summary = "Get buyer requirements for seller", description = "Used by seller to get all buyer requirements")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Buyer requirements fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<BuyingRequirementResponseDto>> getRequirementsForSeller() {
        logger.info("Get buyer requirements for seller");
        logger.info("Buyer requirements for seller fetched successfully");
        return ResponseEntity.ok(
                listingService.getAllRequirementsForSeller());
    }

    @GetMapping("/requirements/contact/{requirementId}")
    @Operation(summary = "Get buyer contact details", description = "Used to get buyer contact information for a requirement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Buyer contact fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Requirement not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public BuyerContactResponseDto getBuyerContact(
            @Parameter(description = "Requirement ID", required = true) @PathVariable Long requirementId) {
        logger.debug("Get buyer contact for buyer with requirement id : {}", requirementId);
        logger.info("Buyer contact fetched successfully");
        return listingService.getBuyerContact(requirementId);
    }

    @GetMapping("/recent-bids")
    @Operation(summary = "Get recent bids", description = "Used by seller to get their recent bids")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recent bids fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<RecentBidResponseDto>> getRecentBids() {
        logger.info("Get recent bids request attempt");
        logger.info("Recent bids fetched successfully");
        return ResponseEntity.ok(listingService.getRecentBids());
    }

    @GetMapping("/my-pending-summary")
    @Operation(summary = "Get pending listings summary", description = "Used by seller to get a summary of their pending listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending listings summary fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<Page<ListingSummaryResponseDto>> pendingSummaryListings(
            @Parameter(description = "Authentication object", required = true) Authentication authentication,
            @Parameter(description = "Pagination and sorting parameters") Pageable pageable) {
        logger.debug("Get all pending listings for user with id : {}", Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        Page<ListingSummaryResponseDto> listings = listingService.pendingSummaryListings(userId, pageable);
        logger.info("Fetched all pending listings for user with id : {} successfully", userId);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/my-sold-summary")
    @Operation(summary = "Get sold listings summary", description = "Used by seller to get a summary of their sold listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sold listings summary fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<Page<ListingSummaryResponseDto>> soldSummaryListings(
            @Parameter(description = "Authentication object", required = true) Authentication authentication,
            @Parameter(description = "Pagination and sorting parameters") Pageable pageable) {
        logger.debug("Get all sold listings for user with id : {}", Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        Page<ListingSummaryResponseDto> listings = listingService.soldSummaryListings(userId, pageable);
        logger.info("Fetched all sold listings for user with id : {} successfully", userId);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/my-closed-summary")
    @Operation(summary = "Get closed listings summary", description = "Used by seller to get a summary of their closed listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Closed listings summary fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<Page<ListingSummaryResponseDto>> closedSummaryListings(
            @Parameter(description = "Authentication object", required = true) Authentication authentication,
            @Parameter(description = "Pagination and sorting parameters") Pageable pageable) {
        logger.debug("Get all closed listings for user with id : {}", Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        Page<ListingSummaryResponseDto> listings = listingService.closedSummaryListings(userId, pageable);
        logger.info("Fetched all closed listings for user with id : {} successfully", userId);
        return ResponseEntity.ok(listings);
    }

//    @GetMapping("/my-active-summary")
//    public ResponseEntity<Page<ListingSummaryResponseDto>> activeSummaryListings(Authentication authentication, Pageable pageable){
//        logger.debug("Get all active listings for user with id : {}",Long.parseLong(authentication.getName()));
//        Long userId = Long.parseLong(authentication.getName());
//        Page<ListingSummaryResponseDto> listings = listingService.activeSummaryListings(userId,pageable);
//        logger.info("Fetched all active listings for user with id : {} successfully",userId);
//        return ResponseEntity.ok(listings);
//    }

    @GetMapping("/my-active-summary")
    @Operation(summary = "Get active listings summary", description = "Used by seller to get a summary of their active listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active listings summary fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<Page<Object>> activeSummaryListings(
            @Parameter(description = "Authentication object", required = true) Authentication authentication,
            @Parameter(description = "Pagination and sorting parameters") Pageable pageable) {

        Long userId = Long.parseLong(authentication.getName());

        logger.debug("Get all active listings for user with id : {}", userId);

        Page<Object> listings = listingService.activeSummaryListings(userId, pageable);

        logger.info("Fetched all active listings for user with id : {} successfully", userId);

        return ResponseEntity.ok(listings);
    }
}
