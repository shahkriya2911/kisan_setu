package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.RequestDto.*;
import com.project.kisan_setu.dto.ResponseDto.*;
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
import com.project.kisan_setu.service.RecentActivityService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final NotificationService notificationService;
    private final FileStorageService fileStorageService;
    private final ValidatorMethods validatorMethods;
    private final BuyerInquiryRepository buyerInquiryRepository;
    private final OrderRepository orderRepository;
    private final BuyingRequirementRepository buyingRequirementRepository;
    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final CropRepository cropRepository;
    private final UnitRepository unitRepository;
    private final StorageRepository storageRepository;
    private final PackagingRepository packagingRepository;
    private final RecentActivityService recentActivityService;
    private static final Logger logger = LoggerFactory.getLogger(ListingServiceImpl.class);


    @Override
    public ListingResponseDto createListing(CreateListingRequest request,
                                            List<MultipartFile> imageFiles,
                                            MultipartFile certificateFile) {
//        validatorMethods.validateUserAccess();  //admin cant create
        logger.info("Creating listing...");
        ProductListingDto productDto = request.getProduct();
        QualityPricingListingDto pricingDto = request.getPricing();
        QualityLocationListingDto locationDto = request.getLocation();
        CropMaster crop = validatorMethods.validateCrop(Long.valueOf(productDto.getCropId()));
        UnitMaster unit = validatorMethods.validateUnit(Long.valueOf(pricingDto.getUnitId()));
        StateMaster state = validatorMethods.validateState(locationDto.getStateId());
        DistrictMaster district = validatorMethods.validateDistrict(locationDto.getDistrictId());
        PackagingMaster packaging = validatorMethods.validatePackaging(Long.valueOf(locationDto.getPackagingId()));
        StorageMaster storage = validatorMethods.validateStorage(Long.valueOf(locationDto.getStorageId()));

        validatePricing(pricingDto);

        logger.info("Mapping images and certificate...");

        logger.info("Creating listing for product: {}", productDto.getCropId());

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
            images.get(0).setIsPrimary(true);
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

        Listing listing = ListingMapper.toEntity(productDto, pricingDto, locationDto,crop,unit,storage,packaging,state,district, images, certificate, description);
        listing.setState(state);
        listing.setDistrict(district);
        Long userId = validatorMethods.getCurrentUserId();
        User seller = validatorMethods.validateUserById(userId);
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
        recentActivityService.logActivity(
                "LISTING_ADDED",
                "Seller " + seller.getFullName() + " added a new crop: " + saved.getCrop(),
                seller.getUserId(),
                null // no buyer yet
        );

        logger.info("Listing created successfully ID: {}", saved.getListingId());
        return ListingMapper.toResponse(saved);
    }

    @Override
    public ListingResponseDto updateListing(Long listingId,
                                            CreateListingRequest request,
                                            List<MultipartFile> imageFiles,
                                            MultipartFile certificateFile) {
//        validatorMethods.validateUserAccess();
        logger.info("Updating listing...");
        logger.info("Validating listing...");
        Listing listing = validatorMethods.validateExists(listingId);


        ProductListingDto productDto = request.getProduct();
        QualityPricingListingDto pricingDto = request.getPricing();
        QualityLocationListingDto locationDto = request.getLocation();
        CropMaster crop = validatorMethods.validateCrop(Long.valueOf(productDto.getCropId()));
        UnitMaster unit = validatorMethods.validateUnit(Long.valueOf(pricingDto.getUnitId()));
        StateMaster state = validatorMethods.validateState(locationDto.getStateId());
        DistrictMaster district = validatorMethods.validateDistrict(locationDto.getDistrictId());
        PackagingMaster packaging = validatorMethods.validatePackaging(Long.valueOf(locationDto.getPackagingId()));
        StorageMaster storage = validatorMethods.validateStorage(Long.valueOf(locationDto.getStorageId()));

        // Validate pricing (includes sale type validation)
        validatePricing(pricingDto);
        String description = request.getDescription();
        ListingMapper.updateEntity(listing, productDto, pricingDto, locationDto, crop,unit,storage,packaging,state,district,description);

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
            updatedImages.get(0).setIsPrimary(true);

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
        logger.info("Validating pricing...");
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
        } else if (pricingDto.getSaleType() == SaleType.FIXED) {
            logger.info("Fixed Price Listing validated");
        } else {
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
        logger.info("Deleting physical file...");
        try {
            Path path = Paths.get("api").resolve(relativePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.error("Failed to delete file: {}", relativePath);
        }
    }

    @Override
    public List<ListingResponseDto> getAllListings() {
//        validatorMethods.validateAdminAccess();
        logger.info("Getting all listings...");
        logger.info("Fetching all listings success...");
        return listingRepository.findAll().stream()
                .map(ListingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ListingResponseDto getListingById(Long id) {
//        validatorMethods.validateUserAccess();
        logger.info("Get listing by id...");
        logger.info("Validating listing...");
        Listing listing = validatorMethods.validateExists(id);
        logger.info("Fetching listing by id success...");
        return ListingMapper.toResponse(listing);
    }

    @Override
    public void deleteListing(Long listingId, Long sellerId) {
//        validatorMethods.validateUserAccess();
        logger.info("Deleting listing....");
        logger.info("Validating listing...");
        Listing listing = validatorMethods.validateExists(listingId);
        validatorMethods.checkStatus(listing, AuctionStatus.SOLD);
        long bidCount = bidRepository.countTotalBidsBySellerId(sellerId);
        if (bidCount > 0) {
            logger.error("Cannot delete listing...Bid already placed...");
            throw new UserException("Cannot delete listing. Bids already placed.");
        }
        logger.info("Listing delete success...");
        listingRepository.delete(listing);
    }


    public Bid placeBid(Long listingId, BigDecimal buyerAmount, Long userId) {
//        validatorMethods.validateUserAccess();
        logger.info("Placing bid for listing....");
        Listing listing = validatorMethods.validateExists(listingId);
        BigDecimal totalBasePrice = listing.getTotalBasePrice();
        BigDecimal minimumBidIncrement = listing.getMinimumBidIncrement();
        long totalBids = bidRepository.countByListing_ListingId(listingId);
        BigDecimal lastbuyerAmount;
        logger.info("Validating bid...");
        Long cuurentUserId = userId;
        Optional<Bid> lastBidOpt = bidRepository.findTopByListingListingIdOrderByBuyerAmountDesc(listingId);
        if (totalBids > 0) {
            if (lastBidOpt.isPresent()){
                Bid lastBid = lastBidOpt.get();
                if (lastBid.getBuyer().getUserId().equals(cuurentUserId)){
                    throw new IllegalStateException("You cannot place two consecutive bids. Wait for another buyer.");
                }
            }
            BigDecimal bidCount = BigDecimal.valueOf(totalBids);

            lastbuyerAmount = totalBasePrice.add(
                    minimumBidIncrement.multiply(bidCount)
            );

            BigDecimal exactRequired = lastbuyerAmount;

            if (buyerAmount.compareTo(exactRequired) != 0) {
                logger.error("Invalid bid! Current base is ₹" + lastbuyerAmount +
                        ". You must bid exactly ₹" + exactRequired +
                        " (increment is fixed at ₹" + minimumBidIncrement + ")");
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
                logger.error("First bid must be exactly ₹" + firstBidRequired +
                        " (Base ₹" + totalBasePrice +
                        " + fixed increment ₹" + minimumBidIncrement + ")");
                throw new UserException(
                        "First bid must be exactly ₹" + firstBidRequired +
                                " (Base ₹" + totalBasePrice +
                                " + fixed increment ₹" + minimumBidIncrement + ")"
                );
            }
        }
        Bid newBid = new Bid();
        newBid.setListing(listing);
        newBid.setBuyerAmount(buyerAmount);
        newBid.setBidTime(LocalDateTime.now());

        Bid saved = bidRepository.save(newBid);
        logger.info("Bid saved — Round: {}, UserID: {}, Amount: ₹{}", totalBids + 1, userId, buyerAmount);
        return saved;
    }

    @Override
    public SellerListingDto getSellerListingDetail(Long listingId) {
//        validatorMethods.validateUserAccess();
        logger.info("Getting seller listing detail...");
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
                            BidStatus.NEW
                    ))
                    .toList();


            long totalBids = bidRepository.countTotalBidsBySellerId(listingId);
            long activeBidders = bidRepository.countActiveBidders(listingId);
            logger.info("Fetching seller listing detail");
            return new SellerListingDto(
                    listing.getListingId(),
                    listing.getCrop().getCropName(),
                    listing.getVariety(),
                    listing.getGrade(),
                    listing.getQuantity(),
                    listing.getPricePerKg(),
                    listing.getUnit().getUnitName(),
                    listing.getTotalBasePrice(),
                    listing.getMinimumBidIncrement(),
                    totalBids,
                    activeBidders,
                    listing.getStatus(),
                    listing.getAuctionEndTime(),
                    currentHighestBid,
                    listing.getPurchaseType(),
                    listing.getSaleType(),
                    listing.getPostedOn(),
                    listing.getState().getName(),
                    listing.getDistrict().getName(),
                    listing.getPickupMethod(),
                    listing.getStorage().getStorageType(),
                    top5Bids,
                    null,
                    ListingMapper.mapImages(listing),
                    listing.getHarvestDate(),
                    listing.getPackaging().getPackagingType(),
                    listing.getRemainingQuantity()
            );
        } else {
            List<BuyerInquiry> inquiries = buyerInquiryRepository.findByListingListingId(listingId);

            List<InquiryResponseDto> inquiryList = inquiries.stream().map(
                    inquiry -> new InquiryResponseDto(
                            inquiry.getInquiryId(),
                            inquiry.getListing().getListingId(),
                            inquiry.getBuyer().getFullName(),
                            inquiry.getListing().getCrop().getCropName(),
                            inquiry.getQuantityRequested(),
                            inquiry.getInquiryTime(),
                            inquiry.getStatus(),
                            inquiry.getRemainingQuantity()
                    )).toList();

            long totalInquires = inquiries.size();
            logger.info("Fetching inquires");
            return new SellerListingDto(
                    listing.getListingId(),
                    listing.getCrop().getCropName(),
                    listing.getVariety(),
                    listing.getGrade(),
                    listing.getQuantity(),
                    listing.getPricePerKg(),
                    listing.getUnit().getUnitName(),
                    listing.getTotalBasePrice(),
                    null,
                    null,
                    null,
                    listing.getStatus(),
                    null,
                    null,
                    listing.getPurchaseType(),
                    listing.getSaleType(),
                    listing.getPostedOn(),
                    listing.getState().getName(),
                    listing.getDistrict().getName(),
                    listing.getPickupMethod(),
                    listing.getStorage().getStorageType(),
                    null,
                    totalInquires,
                    ListingMapper.mapImages(listing),
                    listing.getHarvestDate(),
                    listing.getPackaging().getPackagingType(),
                    listing.getRemainingQuantity()
            );


        }
    }

    @Override
    public DashboardDto getSellerOverview() {
        logger.info("Getting seller overview...");
        Long userId = validatorMethods.getCurrentUserId();
        User seller = validatorMethods.validateUserById(userId);

        Long sellerId = seller.getUserId();
        Long activeListings = listingRepository.countBySeller_UserIdAndStatus(sellerId, AuctionStatus.ACTIVE);
        Long pendingApprovals = listingRepository.countBySeller_UserIdAndStatus(sellerId, AuctionStatus.PENDING);
        Long totalBidsReceived = bidRepository.countTotalBidsBySellerId(sellerId);
        BigDecimal totalRevenue = bidRepository.sumAmountByListingSellerId(sellerId);
        logger.info("Fetching seller overview success...");
        return new DashboardDto(activeListings, pendingApprovals, totalBidsReceived, totalRevenue);
    }

    @Override
    public Order acceptInqury(Long inquiryId, Long userId) {
//        validatorMethods.validateUserAccess();
        logger.info("Accepting inquiry...");
        //convert email to user
        logger.info("Validating user...");
        User seller = validatorMethods.validateUserById(userId);
        BuyerInquiry inquiry = buyerInquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        Listing listing = inquiry.getListing();
        if (!listing.getSeller().getUserId().equals(seller.getUserId())) {
            logger.error("Unauthorized action");
            throw new RuntimeException("Unauthorized action...");
        }

        if (inquiry.getStatus() != InquiryStatus.PENDING) {
            logger.error("Inquiry already processed...");
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
        logger.info("Accept inquiry success...");
        return order;
    }


    public void markAsSold(Long listingId, Long sellerId) {
//        validatorMethods.validateUserAccess();
        logger.info("Marking listing as sold...");
        Listing listing = validatorMethods.validateExists(listingId);
        // Check seller ownership
        if (!listing.getSeller().getUserId().equals(sellerId)) {
            logger.error("You are not authorized to mark this listing as sold");
            throw new UserException("You are not authorized to mark this listing as sold");
        }
        validatorMethods.checkStatus(listing, AuctionStatus.ACTIVE);
        if (listing.getSaleType() == SaleType.AUCTION) {
            if (listing.getAuctionEndTime().isAfter(LocalDateTime.now())) {
                logger.error("auction has not ended yet");
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
        logger.info("Listing marked as sold success....");
        listing.setStatus(AuctionStatus.SOLD);


    }


    @Override
    public Page<ListingResponseDto> myListings(Long userId, Pageable pageable) {
        logger.info("Getting all my listings...");
        Page<Listing> listings = listingRepository.findBySeller_UserId(userId,pageable);
        logger.info("Fetching my listings success...");
        return listings.map(ListingMapper::toResponse);
    }

    @Override
    public Page<ListingResponseDto> activeListings(Long sellerId, Pageable pageable) {
        logger.info("Getting active listings...");
        Page<Listing> listings = listingRepository.findBySeller_UserIdAndStatus(sellerId, AuctionStatus.ACTIVE, pageable);
        return listings.map(ListingMapper::toResponse);
    }

    @Override
    public Page<ListingResponseDto> pendingListings(Long sellerId, Pageable pageable){
        logger.info("Getting pending listings...");
        Page<Listing> listings = listingRepository.findBySeller_UserIdAndStatus(sellerId,AuctionStatus.PENDING,pageable);
        logger.info("Fetching pending listings success...");
        return listings.map(ListingMapper::toResponse);
    }

    @Override
    public Page<ListingResponseDto> soldListings(Long sellerId, Pageable pageable) {
        logger.info("Getting sold closed listings...");
        Page<Listing> listings = listingRepository.findBySeller_UserIdAndStatus(sellerId,AuctionStatus.SOLD,pageable);
        logger.info("Fetching sold listing success...");
        return listings.map(ListingMapper::toResponse);
    }

    @Override
    public Page<ListingResponseDto> closedListings(Long sellerId, Pageable pageable) {
        logger.info("Getting closed listings...");
        Page<Listing> listings = listingRepository.findBySeller_UserIdAndStatus(sellerId,AuctionStatus.CLOSED,pageable);
        logger.info("Fetching closed listings success...");
        return listings.map(ListingMapper::toResponse);
    }

    @Override
    public void extendAuctionTime(Long listingId, Long sellerId, int minutes) {
//        validatorMethods.validateUserAccess();
        logger.info("Extending auction time...");
        Listing listing = validatorMethods.validateExists(listingId);
        validatorMethods.checkStatus(listing, AuctionStatus.ACTIVE);
        if (listing.getAuctionEndTime().isBefore(LocalDateTime.now())) {
            logger.error("Cannot extend expire auction");
            throw new UserException("Cannot Extend expire Auction");
        }
        if (minutes <= 0) {
            logger.error("Extension time must be greater than 0");
            throw new UserException("Extension time must be greater than 0");
        }
        logger.info("Auction time extended success...");
        listing.setAuctionEndTime(listing.getAuctionEndTime().plusMinutes(minutes));
    }

    @Override
    public List<BuyingRequirementResponseDto> getBuyerRequirementsForSeller() {
//        validatorMethods.validateUserAccess();
        logger.info("Getting buyer requirements for seller...");
        logger.info("Validating user...");
        Long userId = validatorMethods.getCurrentUserId();
        User seller = validatorMethods.validateUserById(userId);

        logger.info("Checking if listing exists in DB or not...");
        List<Listing> sellerListings = listingRepository.findBySellerUserId(seller.getUserId());
        List<BuyingRequirement> requirements = new ArrayList<>();
        for (Listing listing : sellerListings) {

            List<BuyingRequirement> requirement =
                    buyingRequirementRepository
                            .findByCropNameIgnoreCase(
                                    listing.getCrop().getCropName());

            requirements.addAll(requirement);
            logger.info("Validating requirements...");
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
        logger.info("Fetching buyer requirements success...");
        return requirements.stream()
                .map(BuyingRequirementMapper::toDto)
                .toList();
    }

    @Override
    public BuyerContactResponseDto getBuyerContact(Long requirementId) {
//        validatorMethods.validateUserAccess();
        logger.info("Getting buyer contact...");
        BuyingRequirement requirement =
                buyingRequirementRepository.findById(requirementId)
                        .orElseThrow(() -> new RuntimeException("Requirement not found"));

        User buyer = requirement.getBuyer();
        logger.info("Fetching buyer contact success...");
        return new BuyerContactResponseDto(
                requirement.getRequirementId(),
                buyer.getFullName(),
                buyer.getMobileNumber(),
                requirement.getCropName(),
                requirement.getQuantityRequired(),
                requirement.getUnit()
        );
    }

    @Override
    public List<RecentBidResponseDto> getRecentBids() {
        logger.info("Getting recent bids...");
        logger.info("Validating user...");
        Long userId = validatorMethods.getCurrentUserId();
        User seller = validatorMethods.validateUserById(userId);

        List<Bid> bids = bidRepository
                .findTop5ByListingSellerUserIdAndBidStatusOrderByCreatedAtDesc(
                        seller.getUserId(), BidStatus.NEW);

        List<RecentBidResponseDto> response = new ArrayList<>();

        for (Bid bid : bids) {
            response.add(RecentBidResponseDto.builder()
                    .bidderId(bid.getBidId())
                    .bidderName(bid.getBuyer().getFullName())
                    .cropListing(bid.getListing().getCrop().getCropName())
                    .bidAmount(bid.getBuyerAmount())
                    .timestamp(bid.getCreatedAt())
                    .bidStatus(bid.getBidStatus().name())
                    .build());
        }
        logger.info("Fetching recent bids success...");
        return response;
    }

    @Override
    public String acceptBid(Long bidId) {
        logger.info("Accepting bid...");
        logger.info("Validation user...");
        Long userId = validatorMethods.getCurrentUserId();

        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        if (!bid.getListing().getSeller().getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to accept this bid");
        }

        if (bid.getBidStatus() != BidStatus.NEW) {
            throw new RuntimeException("Bid is already " + bid.getBidStatus());
        }

        bid.setBidStatus(BidStatus.ACCEPTED);
        bidRepository.save(bid);
        logger.info("Bid accepted success...");
        return "Bid Accepted Successfully";
    }

    @Override
    public String rejectBid(Long bidId) {
        logger.info("Rejecting bid...");
        logger.info("Validating user...");
        Long userId = validatorMethods.getCurrentUserId();

        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        if (!bid.getListing().getSeller().getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to reject this bid");
        }

        if (bid.getBidStatus() != BidStatus.NEW) {
            throw new RuntimeException("Bid is already " + bid.getBidStatus());
        }

        bid.setBidStatus(BidStatus.REJECTED);
        bidRepository.save(bid);
        logger.info("Bid rejected success...");
        return "Bid Rejected Successfully";
    }

    @Override
    public Page<ListingSummaryResponseDto> activeSummaryListings(Long sellerId, Pageable pageable) {
        logger.info("Getting active listings...");
        Page<Listing> listings = listingRepository.findBySeller_UserIdAndStatus(sellerId, AuctionStatus.ACTIVE, pageable);
        logger.info("Fetching active listings success...");
        return listings.map(listing -> {
            ListingSummaryResponseDto dto = ListingMapper.toSummaryResponse(listing);
            dto.setCurrentHighestBid(resolveCurrentHighestBid(listing));
            return dto;
        });
    }

    @Override
    public Page<ListingSummaryResponseDto> pendingSummaryListings(Long sellerId, Pageable pageable){
        logger.info("Getting pending listings...");
        Page<Listing> listings = listingRepository.findBySeller_UserIdAndStatus(sellerId,AuctionStatus.PENDING,pageable);
        logger.info("Fetching pending listings success...");
        return listings.map(listing -> {
            ListingSummaryResponseDto dto = ListingMapper.toSummaryResponse(listing);
            dto.setCurrentHighestBid(resolveCurrentHighestBid(listing));
            return dto;
        });
    }

    @Override
    public Page<ListingSummaryResponseDto> soldSummaryListings(Long sellerId, Pageable pageable) {
        logger.info("Getting sold closed listings...");
        Page<Listing> listings = listingRepository.findBySeller_UserIdAndStatus(sellerId,AuctionStatus.SOLD,pageable);
        logger.info("Fetching sold listing success...");
        return listings.map(listing -> {
            ListingSummaryResponseDto dto = ListingMapper.toSummaryResponse(listing);
            dto.setCurrentHighestBid(resolveCurrentHighestBid(listing));
            return dto;
        });
    }

    @Override
    public Page<ListingSummaryResponseDto> closedSummaryListings(Long sellerId, Pageable pageable) {
        logger.info("Getting closed listings...");
        Page<Listing> listings = listingRepository.findBySeller_UserIdAndStatus(sellerId,AuctionStatus.CLOSED,pageable);
        logger.info("Fetching closed listings success...");
        return listings.map(listing -> {
            ListingSummaryResponseDto dto = ListingMapper.toSummaryResponse(listing);
            dto.setCurrentHighestBid(resolveCurrentHighestBid(listing));
            return dto;
        });
    }

    private BigDecimal resolveCurrentHighestBid(Listing listing) {
        BigDecimal basePrice = listing.getTotalBasePrice();
        if (basePrice == null && listing.getPricePerKg() != null && listing.getQuantity() != null) {
            basePrice = listing.getPricePerKg().multiply(listing.getQuantity());
        }

        BigDecimal resolvedBasePrice = basePrice != null ? basePrice : listing.getPricePerKg();
        BigDecimal fallback = listing.getPurchaseType() == PurchaseType.WHOLE_LOT_ONLY
                ? resolvedBasePrice
                : listing.getPricePerKg();

        return bidRepository
                .findTopByListingListingIdOrderByBuyerAmountDesc(listing.getListingId())
                .map(Bid::getBuyerAmount)
                .orElse(fallback);
    }
}


