package com.project.kisan_setu.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.kisan_setu.dto.RequestDto.CreateListingRequest;
import com.project.kisan_setu.dto.RequestDto.ExtendAuctionDto;
import com.project.kisan_setu.dto.ResponseDto.DashboardDto;
import com.project.kisan_setu.dto.ResponseDto.ListingResponseDto;
import com.project.kisan_setu.dto.ResponseDto.SellerListingDto;
import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.dto.SellerListingFixedDto;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.BidStatus;
import com.project.kisan_setu.dto.RequestDto.CreateListingRequest;
import com.project.kisan_setu.dto.ResponseDto.DashboardDto;
import com.project.kisan_setu.dto.ResponseDto.*;
import com.project.kisan_setu.service.ListingService;
import com.project.kisan_setu.service.UserService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
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
        logger.debug("Create listing request attempt by user");
        CreateListingRequest request = objectMapper.readValue(requestJson, CreateListingRequest.class);
        Long sellerId = validatorMethods.getCurrentUserId();
        logger.info("listing created successfully for user with id : {}",sellerId);
        return ResponseEntity.ok(listingService.createListing(request, imageFiles, certificateFile));
    }

    @PutMapping(value = "/{listingId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ListingResponseDto> updateListing(
            @PathVariable Long listingId,
            @RequestPart("data") CreateListingRequest request,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestPart(value = "certificateFile", required = false) MultipartFile certificateFile
    ) {
        logger.debug("Update listing request attempt for listing with id : {}",listingId);
        logger.info("Update listing successful for listing with id : {}",listingId);
        return ResponseEntity.ok(listingService.updateListing(listingId, request, imageFiles, certificateFile));
    }

    @GetMapping
    public ResponseEntity<List<ListingResponseDto>> getAllListings() {
        logger.info("Get all listings request attempt");
        logger.info("Fetched all listings successfully");
        return ResponseEntity.ok(listingService.getAllListings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponseDto> getListingById(@PathVariable Long id) {
        logger.debug("Get listing by id attempt for listing with id : {}",id);
        logger.info("Listing with id : {} fetched successfully",id);
        return ResponseEntity.ok(listingService.getListingById(id));
    }

    @DeleteMapping("/{listingId}")
    public ResponseEntity<String> deleteListing(@PathVariable Long listingId,@RequestParam Long sellerId) {
        logger.debug("Delete listing with id request attempt for listing with id : {} by user with id : {}",listingId,sellerId);
        listingService.deleteListing(listingId,sellerId);
        logger.info("Delete listing with id : {} successful",listingId);
        return ResponseEntity.ok("Listing Deleted Successfully");

    }

    @GetMapping("/{listingId}/top-5")
    public ResponseEntity<SellerListingDto> getAuctionListingDetail(
            @PathVariable Long listingId) {
        logger.debug("Get top 5 bids for listing with id : {} request attempt",listingId);
        Long sellerId = validatorMethods.getCurrentUserId();
        logger.info("Fetched top 5 bids successfully for listing with id : {}",listingId);
        return ResponseEntity.ok(listingService.getSellerAuctionListingDetail(listingId));
    }

    @GetMapping("/{listingId}/fixed")
    public ResponseEntity<SellerListingFixedDto> getFixedListingDetail(
            @PathVariable Long listingId
    ){
        logger.debug("Get fixed listing with id : {} request attempt",listingId);
        Long sellerId = validatorMethods.getCurrentUserId();
        logger.info("Fetched fixed listing with id : {}",listingId);
        return ResponseEntity.ok(listingService.getSellerFixedListingDetail(listingId));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getSellerOverView(){
        logger.info("Get seller overview request");
        logger.info("Seller overview fetched successfully");
        return ResponseEntity.ok(listingService.getSellerOverview());
    }


    @PutMapping("/seller/{listingId}/mark-sold")
    public ResponseEntity<String> markAsSold(
            @PathVariable Long listingId,
            @RequestParam Long sellerId) {
        logger.debug("Listing marked as sold request attempt for listing with id : {}",listingId);
        listingService.markAsSold(listingId, sellerId);
        logger.info("Listing marked as sold successfully");
        return ResponseEntity.ok("Listing marked as SOLD successfully");
    }

    @PostMapping("/extend-auction")
    public ResponseEntity<String> extendAuction(
            @RequestBody ExtendAuctionDto dto) {

        String response = listingService.extendAuctionTime(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-pending")
    public ResponseEntity<Page<ListingResponseDto>> pendingListings(Authentication authentication, Pageable pageable){
        logger.debug("Get all pending listings for user with id : {}",Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        Page<ListingResponseDto> listings = listingService.pendingListings(userId,pageable);
        logger.info("Fetched all pending listings for user with id : {} successfully",userId);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/my-sold")
    public ResponseEntity<Page<ListingResponseDto>> soldListings(Authentication authentication, Pageable pageable){
        logger.debug("Get all sold listings for user with id : {}",Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        Page<ListingResponseDto> listings = listingService.soldListings(userId,pageable);
        logger.info("Fetched all sold listings for user with id : {} successfully",userId);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/my-closed")
    public ResponseEntity<Page<ListingResponseDto>> closedListings(Authentication authentication, Pageable pageable){
        logger.debug("Get all closed listings for user with id : {}",Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        Page<ListingResponseDto> listings = listingService.closedListings(userId,pageable);
        logger.info("Fetched all closed listings for user with id : {} successfully",userId);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/my-active")
    public ResponseEntity<Page<ListingResponseDto>> activeListings(Authentication authentication, Pageable pageable){
        logger.debug("Get all active listings for user with id : {}",Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        Page<ListingResponseDto> listings = listingService.activeListings(userId,pageable);
        logger.info("Fetched all active listings for user with id : {} successfully",userId);
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
    public ResponseEntity<Page<ListingResponseDto>> myListings(Authentication authentication,Pageable pageable){
        logger.debug("Get all my listings for user with id : {}",Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        Page<ListingResponseDto> listings = listingService.myListings(userId,pageable);
        logger.info("Fetched all my listings for user with id : {} successfully",userId);
        return ResponseEntity.ok(listings);
    }


    @GetMapping("/seller/requirements")
    public ResponseEntity<List<BuyingRequirementResponseDto>> getRequirementsForSeller() {
        logger.info("Get buyer requirements for seller");
        logger.info("Buyer requirements for seller fetched successfully");
        return ResponseEntity.ok(
                listingService.getBuyerRequirementsForSeller()
        );
    }
    @GetMapping("/requirements/contact/{requirementId}")
    public BuyerContactResponseDto getBuyerContact(
            @PathVariable Long requirementId) {
        logger.debug("Get buyer contact for buyer with requirement id : {}",requirementId);
        logger.info("Buyer contact fetched successfully");
        return listingService.getBuyerContact(requirementId);
    }
    @GetMapping("/recent-bids")
    public ResponseEntity<List<RecentBidResponseDto>> getRecentBids() {
        logger.info("Get recent bids request attempt");
        logger.info("Recent bids fetched successfully");
        return ResponseEntity.ok(listingService.getRecentBids());
    }

    @PutMapping("/recent-bids/{bidId}/accept")
    public ResponseEntity<String> acceptBid(@PathVariable Long bidId) {
        logger.debug("Accept bid request attempt for bid with id : {} ",bidId);
        logger.info("Bid accepted by user successfully");
        return ResponseEntity.ok(listingService.acceptBid(bidId));
    }

    @PutMapping("/recent-bids/{bidId}/reject")
    public ResponseEntity<String> rejectBid(@PathVariable Long bidId) {
        logger.debug("Reject bid request attempt for bid with id : {}",bidId);
        logger.info("Bid rejected bu user successfully");
        return ResponseEntity.ok(listingService.rejectBid(bidId));
    }

    @GetMapping("/my-pending-summary")
    public ResponseEntity<Page<ListingSummaryResponseDto>> pendingSummaryListings(Authentication authentication, Pageable pageable){
        logger.debug("Get all pending listings for user with id : {}",Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        Page<ListingSummaryResponseDto> listings = listingService.pendingSummaryListings(userId,pageable);
        logger.info("Fetched all pending listings for user with id : {} successfully",userId);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/my-sold-summary")
    public ResponseEntity<Page<ListingSummaryResponseDto>> soldSummaryListings(Authentication authentication, Pageable pageable){
        logger.debug("Get all sold listings for user with id : {}",Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        Page<ListingSummaryResponseDto> listings = listingService.soldSummaryListings(userId,pageable);
        logger.info("Fetched all sold listings for user with id : {} successfully",userId);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/my-closed-summary")
    public ResponseEntity<Page<ListingSummaryResponseDto>> closedSummaryListings(Authentication authentication, Pageable pageable){
        logger.debug("Get all closed listings for user with id : {}",Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        Page<ListingSummaryResponseDto> listings = listingService.closedSummaryListings(userId,pageable);
        logger.info("Fetched all closed listings for user with id : {} successfully",userId);
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
    public ResponseEntity<Page<Object>> activeSummaryListings(
            Authentication authentication,
            Pageable pageable
    ) {

        Long userId = Long.parseLong(authentication.getName());

        logger.debug("Get all active listings for user with id : {}", userId);

        Page<Object> listings = listingService.activeSummaryListings(userId, pageable);

        logger.info("Fetched all active listings for user with id : {} successfully", userId);

        return ResponseEntity.ok(listings);
    }
}
