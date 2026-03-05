package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.embedded.ListingCertificate;
import com.project.kisan_setu.embedded.ListingImage;
import com.project.kisan_setu.entity.*;
import com.project.kisan_setu.enums.*;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.BuyingRequirementMapper;
import com.project.kisan_setu.mapper.ListingMapper;
import com.project.kisan_setu.repository.*;
import com.project.kisan_setu.service.FileStorageService;
import com.project.kisan_setu.service.ListingService;
import com.project.kisan_setu.service.NotificationService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final BidHistoryRepository bidHistoryRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final NotificationService notificationService;
    private final FileStorageService fileStorageService;
    private final ValidatorMethods validatorMethods;
    private final BuyerInquiryRepository buyerInquiryRepository;
    private final OrderRepository orderRepository;
    private final BuyingRequirementRepository buyingRequirementRepository;
    private static final Logger logger = LoggerFactory.getLogger(ListingServiceImpl.class);



    @Override
    public ListingResponseDto createListing(CreateListingRequest request,
                                            List<MultipartFile> imageFiles,
                                            MultipartFile certificateFile) {

        ProductListingDto productDto = request.getProduct();
        QualityPricingListingDto pricingDto = request.getPricing();
        QualityLocationListingDto locationDto = request.getLocation();

        validatePricing(pricingDto);

        logger.info("Creating listing for product: {}", productDto.getCropName());

        // Map images
        List<ListingImage> images = null;
        if (imageFiles != null && !imageFiles.isEmpty()) {
            images = imageFiles.stream().map(file -> {
                String path = fileStorageService.storeFile(file, "images");
                ListingImage image = new ListingImage();
                image.setFileName(file.getOriginalFilename());
                image.setFilePath(path);
                image.setFileType(file.getContentType());
                image.setIsPrimary(false);
                return image;
            }).collect(Collectors.toList());
        }

        // Map certificate
        ListingCertificate certificate = null;
        if (certificateFile != null && !certificateFile.isEmpty()) {
            String path = fileStorageService.storeFile(certificateFile, "certificates");
            certificate = new ListingCertificate();
            certificate.setFileName(certificateFile.getOriginalFilename());
            certificate.setFilePath(path);
            certificate.setFileType(certificateFile.getContentType());
        }

        String description = request.getDescription();

        Listing listing = ListingMapper.toEntity(productDto, pricingDto, locationDto, images, certificate,description);
        Long userId = validatorMethods.getCurrentUserId();
        User seller =  validatorMethods.validateUserById(userId);
        listing.setSeller(seller);

        if (listing.getSaleType() == SaleType.AUCTION) {

            if (listing.getAuctionEndTime().isAfter(LocalDateTime.now())) {
                listing.setStatus(AuctionStatus.ACTIVE);
            } else {
                listing.setStatus(AuctionStatus.CLOSED);
            }

        } else {
            listing.setStatus(AuctionStatus.ACTIVE);
        }

        BigDecimal totalBasePrice =
                pricingDto.getPricePerKg()
                        .multiply(pricingDto.getQuantity());
        listing.setTotalBasePrice(totalBasePrice);
        listing.setRemainingQuantity(pricingDto.getQuantity());

        Listing saved = listingRepository.save(listing);

        logger.info("Listing created successfully ID: {}", saved.getListingId());
        return ListingMapper.toResponse(saved);
    }

    @Override
    public ListingResponseDto updateListing(Long listingId,
                                            CreateListingRequest request,
                                            List<MultipartFile> imageFiles,
                                            MultipartFile certificateFile) {

        Listing listing = validatorMethods.validateExists(listingId);

        ProductListingDto productDto = request.getProduct();
        QualityPricingListingDto pricingDto = request.getPricing();
        QualityLocationListingDto locationDto = request.getLocation();

        // Validate pricing (includes sale type validation)
        validatePricing(pricingDto);
        String description = request.getDescription();
        ListingMapper.updateEntity(listing, productDto, pricingDto, locationDto,description);

        // Update images if new files provided
        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<ListingImage> updatedImages = imageFiles.stream().map(file -> {
                String path = fileStorageService.storeFile(file, "images");
                ListingImage image = new ListingImage();
                image.setFileName(file.getOriginalFilename());
                image.setFilePath(path);
                image.setFileType(file.getContentType());
                image.setIsPrimary(false);
                return image;
            }).collect(Collectors.toList());

            listing.getImages().clear();
            listing.setImages(updatedImages);
        }

        // Update certificate if new file provided
        if (certificateFile != null && !certificateFile.isEmpty()) {
            if (listing.getCertificate() != null) {
                deletePhysicalFile(listing.getCertificate().getFilePath());
            }
            String path = fileStorageService.storeFile(certificateFile, "certificates");
            ListingCertificate certificate = new ListingCertificate();
            certificate.setFileName(certificateFile.getOriginalFilename());
            certificate.setFilePath(path);
            certificate.setFileType(certificateFile.getContentType());
            listing.setCertificate(certificate);
        }

        Listing updated = listingRepository.save(listing);
        logger.info("Listing Updated Successfully ID: {}", updated.getListingId());
        return ListingMapper.toResponse(updated);
    }
    private void validatePricing(QualityPricingListingDto pricingDto) {

        if (pricingDto.getQuantity() == null ||
                pricingDto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserException("Quantity must be greater than 0");
        }

        if (pricingDto.getPricePerKg() == null ||
                pricingDto.getPricePerKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserException("PricePerKg must be greater than 0");
        }

        if (pricingDto.getSaleType() == null) {
            throw new UserException("SaleType must be specified");
        }

        if (pricingDto.getSaleType() == SaleType.AUCTION) {

            if (pricingDto.getAuctionEndTime() == null) {
                throw new UserException("Auction End Time required");
            }

            if (pricingDto.getAuctionEndTime().isBefore(LocalDateTime.now())) {
                throw new UserException("Auction has ended");
            }

            if (pricingDto.getMinimumBidIncrement() == null ||
                    pricingDto.getMinimumBidIncrement()
                            .compareTo(BigDecimal.ZERO) <= 0) {

                throw new UserException("Minimum Bid Increment must be greater than 0");
            }

            logger.info("Auction Listing validated");
        }
        else if (pricingDto.getSaleType() == SaleType.FIXED) {
            logger.info("Fixed Price Listing validated");
        }

        else {
            throw new UserException("SaleType must be AUCTION or FIXED");
        }
        if (pricingDto.getPurchaseType() == PurchaseType.PARTIAL_ORDER_ALLOWS) {

            if (pricingDto.getMinimumOrderQuantity() == null ||
                    pricingDto.getMinimumOrderQuantity()
                            .compareTo(BigDecimal.ZERO) <= 0) {

                throw new UserException("Minimum Order Quantity must be greater than 0");
            }

            if (pricingDto.getMoqPricePerKg() == null ||
                    pricingDto.getMoqPricePerKg()
                            .compareTo(BigDecimal.ZERO) <= 0) {

                throw new UserException("MOQ PricePerKg must be greater than 0");
            }

            logger.info("Partial Order Listing validated");
        }
    }

    private void deletePhysicalFile(String relativePath) {
        try {
            Path path = Paths.get("uploads").resolve(relativePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.warn("Failed to delete file: {}", relativePath);
        }
    }

    @Override
    public List<ListingResponseDto> getAllListings() {
        return listingRepository.findAll().stream()
                .map(ListingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ListingResponseDto getListingById(Long id) {
        Listing listing = validatorMethods.validateExists(id);
        return ListingMapper.toResponse(listing);
    }

    @Override
    public void deleteListing(Long listingId,Long sellerId) {
        Listing listing = validatorMethods.validateExists(listingId);
        validatorMethods.checkStatus(listing,AuctionStatus.SOLD);
        long bidCount = bidRepository.countTotalBidsBySellerId(sellerId);
        if (bidCount > 0) {
            throw new UserException("Cannot delete listing. Bids already placed.");
        }

        listingRepository.delete(listing);
    }


    public BidHistory placeBid(Long listingId,BigDecimal buyerAmount, Long userId) {
        Listing listing = validatorMethods.validateExists(listingId);
        BigDecimal totalBasePrice = listing.getTotalBasePrice();
        BigDecimal minimumBidIncrement = listing.getMinimumBidIncrement();
        long totalBids = bidHistoryRepository.countByListing_ListingId(listingId);
        BigDecimal lastbuyerAmount;

        if (totalBids > 0) {

            BigDecimal bidCount = BigDecimal.valueOf(totalBids);

            lastbuyerAmount = totalBasePrice.add(
                    minimumBidIncrement.multiply(bidCount)
            );

            BigDecimal exactRequired = lastbuyerAmount;

            if (buyerAmount.compareTo(exactRequired) != 0) {
                throw new UserException(
                        "Invalid bid! Current base is ₹" + lastbuyerAmount +
                                ". You must bid exactly ₹" + exactRequired +
                                " (increment is fixed at ₹" + minimumBidIncrement + ")"
                );
            }
        } else {
            BigDecimal firstBidRequired =
                    totalBasePrice.add(minimumBidIncrement);

            if (buyerAmount.compareTo(firstBidRequired) != 0) {
                throw new UserException(
                        "First bid must be exactly ₹" + firstBidRequired +
                                " (Base ₹" + totalBasePrice +
                                " + fixed increment ₹" + minimumBidIncrement + ")"
                );
            }
        }
        BidHistory newBid = new BidHistory();
        newBid.setListing(listing);
        newBid.setAmountPerKg(buyerAmount);
        newBid.setBidTime(LocalDateTime.now());

        BidHistory saved = bidHistoryRepository.save(newBid);
        logger.info("Bid saved — Round: {}, UserID: {}, Amount: ₹{}", totalBids + 1, userId, buyerAmount);
        return saved;
    }

    @Override
    public SellerListingDto getSellerListingDetail(Long listingId) {

         Listing listing = validatorMethods.validateExists(listingId);

         if (listing.getSaleType() == SaleType.AUCTION) {
             BigDecimal currentHighestBid =
                     bidRepository.findTopByListingListingIdOrderByBuyerAmountDesc(listingId)
                             .map(Bid::getBuyerAmount)
                             .orElse(listing.getTotalBasePrice());


             List<BidResponseDto> top5Bids = bidRepository
                     .findTop5ByListingListingIdOrderByBuyerAmountDesc(listingId)
                     .stream()
                     .map(bid -> new BidResponseDto(
                             bid.getBuyer().getUserId(),
                             bid.getBuyerAmount(),
                             bid.getBuyer().getFullName(),
                             bid.getBidTime(),
                             listing.getRemainingQuantity()
                     ))
                     .toList();


             long totalBids = bidRepository.countTotalBidsBySellerId(listingId);
             long activeBidders = bidRepository.countActiveBidders(listingId);

             return new SellerListingDto(
                     listing.getListingId(),
                     listing.getCropName(),
                     listing.getVariety(),
                     listing.getGrade(),
                     listing.getQuantity(),
                     listing.getPricePerKg(),
                     listing.getUnit(),
                     listing.getTotalBasePrice(),
                     listing.getMinimumBidIncrement(),
                     listing.getState(),
                     listing.getDistrict(),
                     totalBids,
                     activeBidders,
                     listing.getStatus(),
                     listing.getAuctionEndTime(),
                     currentHighestBid,
                     listing.getPurchaseType(),
                     listing.getSaleType(),
                     listing.getPostedOn(),
                     top5Bids,
                     null
             );
         }else{
             List<BuyerInquiry> inquiries = buyerInquiryRepository.findByListingListingId(listingId);

             List<InquiryResponseDto> inquiryList = inquiries.stream().map(
                     inquiry -> new InquiryResponseDto(
                             inquiry.getInquiryId(),
                     inquiry.getListing().getListingId(),
                     inquiry.getBuyer().getFullName(),
                     inquiry.getListing().getCropName(),
                     inquiry.getQuantityRequested(),
                     inquiry.getInquiryTime(),
                     inquiry.getStatus()
             )).toList();

             long totalInquires = inquiries.size();
             return new SellerListingDto(
                     listing.getListingId(),
                     listing.getCropName(),
                     listing.getVariety(),
                     listing.getGrade(),
                     listing.getQuantity(),
                     listing.getPricePerKg(),
                     listing.getUnit(),
                     listing.getTotalBasePrice(),
                   null,
                     listing.getState(),
                     listing.getDistrict(),
                    null,
                     null,
                     listing.getStatus(),
                     null,
                     null,
                     listing.getPurchaseType(),
                     listing.getSaleType(),
                     listing.getPostedOn(),
                     null,
                     totalInquires
             );


         }
    }

    @Override
    public DashboardDto getSellerOverview() {
        Long userId = validatorMethods.getCurrentUserId();
        User seller = validatorMethods.validateUserById(userId);

        Long sellerId = seller.getUserId();
        Long activeListings = listingRepository.countBySeller_UserIdAndStatus(sellerId, AuctionStatus.ACTIVE);
        Long pendingApprovals = listingRepository.countBySeller_UserIdAndStatus(sellerId, AuctionStatus.PENDING);
        Long totalBidsReceived = bidRepository.countTotalBidsBySellerId(sellerId);
        BigDecimal totalRevenue = bidRepository.sumAmountByListingSellerId(sellerId);

        return new DashboardDto(activeListings, pendingApprovals, totalBidsReceived, totalRevenue);
    }

    @Override
    public Order acceptInqury(Long inquiryId, Long userId) {
        //convert email to user
        User seller = validatorMethods.validateUserById(userId);
        BuyerInquiry inquiry = buyerInquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        Listing listing = inquiry.getListing();
        if (!listing.getSeller().getUserId().equals(seller.getUserId())) {
            throw new RuntimeException("Unauthorized action");
        }

        if (inquiry.getStatus() != InquiryStatus.PENDING) {
            throw new RuntimeException("Inquiry already processed");
        }

        inquiry.setStatus(InquiryStatus.ACCEPTED);
        inquiry.setRespondedAt(LocalDateTime.now());
        buyerInquiryRepository.save(inquiry);
        Order order = new Order();
        order.setListing(listing);
        order.setBuyer(inquiry.getBuyer());
        order.setQuantity(inquiry.getQuantityRequested());
        order.setOrderTime(LocalDateTime.now());
        orderRepository.save(order);
        return order;
    }



    public void markAsSold(Long listingId,Long sellerId) {
        Listing listing = validatorMethods.validateExists(listingId);
        // Check seller ownership
        if (!listing.getSeller().getUserId().equals(sellerId)) {
            throw new UserException("You are not authorized to mark this listing as sold");
        }
        validatorMethods.checkStatus(listing, AuctionStatus.ACTIVE);
        if (listing.getSaleType() == SaleType.AUCTION) {
            if (listing.getAuctionEndTime().isAfter(LocalDateTime.now())) {
                throw new UserException("auction has not ended yet");
            }
            Bid highestBid = bidRepository
                    .findTopByListingListingIdOrderByBuyerAmountDesc(listingId)
                    .orElseThrow(() ->
                            new UserException("Cannot mark as sold. No bids placed."));

            Order order = new Order();
            order.setBuyer(highestBid.getBuyer());
            order.setListing(listing);
            order.setQuantity(listing.getQuantity());
            order.setPricePerKg(highestBid.getBuyerAmount());
            order.setTotalBasePrice(
                    highestBid.getBuyerAmount()
                            .multiply(listing.getQuantity())
            );
            order.setOrderTime(LocalDateTime.now());

            orderRepository.save(order);
        } else {
            BuyerInquiry acceptedInquiry = buyerInquiryRepository
                    .findByListingListingIdAndStatus(listingId, InquiryStatus.ACCEPTED)
                    .orElseThrow(() ->
                            new UserException("No accepted inquiry found."));
            Order order = new Order();
            order.setBuyer(acceptedInquiry.getBuyer());
            order.setListing(listing);
            order.setQuantity(acceptedInquiry.getQuantityRequested());
            order.setOrderTime(LocalDateTime.now());

            orderRepository.save(order);
        }
        listing.setStatus(AuctionStatus.SOLD);


    }

    @Override
    public Page<ListingResponseDto> activeListings(Long sellerId, Pageable pageable){
        Page<Listing> listings = listingRepository.findBySeller_UserIdAndStatus(sellerId,AuctionStatus.ACTIVE,pageable);
        return listings.map(ListingMapper::toResponse);
    }

    @Override
    public void extendAuctionTime(Long listingId, Long sellerId, int minutes) {
        Listing listing = validatorMethods.validateExists(listingId);
       validatorMethods.checkStatus(listing,AuctionStatus.ACTIVE);
        if(listing.getAuctionEndTime().isBefore(LocalDateTime.now())){
            throw new UserException("Cannot Extend expire Auction");
        }
        if(minutes <= 0){
            throw new UserException("Extension time must be greater than 0");
        }
        listing.setAuctionEndTime(listing.getAuctionEndTime().plusMinutes(minutes));
    }
    @Override
    public List<BuyingRequirementResponseDto> getBuyerRequirementsForSeller() {
        Long userId= validatorMethods.getCurrentUserId();
        User seller = validatorMethods.validateUserById(userId);

        List<Listing> sellerListings = listingRepository.findBySellerUserId(seller.getUserId());
        List<BuyingRequirement> requirements = new ArrayList<>();
        for (Listing listing : sellerListings) {

            List<BuyingRequirement> requirement =
                    buyingRequirementRepository
                            .findByCropNameIgnoreCase(
                                    listing.getCropName());

            requirements.addAll(requirement);
            for (BuyingRequirement req : requirements) {

                // Skip if seller is same as buyer
                if (!req.getBuyer().getUserId().equals(seller.getUserId())) {
                    continue;
                }
                if (req.getDeadline() != null &&
                        req.getDeadline().isBefore(ChronoLocalDate.from(LocalDateTime.now()))) {
                    continue;
                }
                if (req.getRequirementStatus() != RequirementStatus.ACTIVE) {
                    continue;
                }
                if (listing.getQuantity()
                        .compareTo(req.getQuantityRequired()) < 0) {
                    continue;
                }
                requirements.add(req);
            }
        }

        return requirements.stream()
                .map(BuyingRequirementMapper::toDto)
                .toList();
    }

    @Override
    public BuyerContactResponseDto getBuyerContact(Long requirementId) {
        BuyingRequirement requirement =
                buyingRequirementRepository.findById(requirementId)
                        .orElseThrow(() -> new RuntimeException("Requirement not found"));

        User buyer = requirement.getBuyer();

        return new BuyerContactResponseDto(
                requirement.getRequirementId(),
                buyer.getFullName(),
                buyer.getMobileNumber(),
                requirement.getCropName(),
                requirement.getQuantityRequired(),
                requirement.getUnit()
        );
    }

}