package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.embedded.ListingCertificate;
import com.project.kisan_setu.embedded.ListingImage;
import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.BidHistory;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.PurchaseType;
import com.project.kisan_setu.enums.SaleType;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.ListingMapper;
import com.project.kisan_setu.repository.*;
import com.project.kisan_setu.service.FileStorageService;
import com.project.kisan_setu.service.ListingService;
import com.project.kisan_setu.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final BidHistoryRepository bidHistoryRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final NotificationService notificationService;
    private final FileStorageService fileStorageService;
    private static final Logger logger = LoggerFactory.getLogger(ListingServiceImpl.class);


    public ListingServiceImpl(ListingRepository listingRepository,
                              BidHistoryRepository bidHistoryRepository,
                              UserRepository userRepository,
                              BidRepository bidRepository,
                              NotificationService notificationService,
                              FileStorageService fileStorageService) {
        this.listingRepository = listingRepository;
        this.bidHistoryRepository = bidHistoryRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
        this.notificationService = notificationService;
        this.fileStorageService = fileStorageService;
    }

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

        // Map entity
        Listing listing = ListingMapper.toEntity(productDto, pricingDto, locationDto, images, certificate);

        // Set seller & status
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        listing.setSeller(seller);
        listing.setStatus(AuctionStatus.ACTIVE);
        listing.setRemainingQuantity(pricingDto.getRemainingQuantity());
        // Total Base Price
        listing.setTotalBasePrice(pricingDto.getPricePerKg() * pricingDto.getQuantity());

        // Remaining Quantity
        listing.setRemainingQuantity(pricingDto.getRemainingQuantity());

        // Save
        Listing saved = listingRepository.save(listing);

        notificationService.createNotification(seller.getUserId(), "Crop Created Successfully");
        logger.info("Listing Created Successfully ID: {}", saved.getListingId());


        return ListingMapper.toResponse(saved);
    }

    // UPDATE LISTING
    @Override
    public ListingResponseDto updateListing(Long listingId,
                                            CreateListingRequest request,
                                            List<MultipartFile> imageFiles,
                                            MultipartFile certificateFile) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new UserException("Listing not found with id: " + listingId));

        ProductListingDto productDto = request.getProduct();
        QualityPricingListingDto pricingDto = request.getPricing();
        QualityLocationListingDto locationDto = request.getLocation();

        // Validate pricing (includes sale type validation)
        validatePricing(pricingDto);

        // Update basic fields
        ListingMapper.updateEntity(listing, productDto, pricingDto, locationDto);

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
        if (pricingDto.getQuantity() == null || pricingDto.getQuantity() <= 0)
            throw new UserException("Quantity must be greater than 0");

        if (pricingDto.getPricePerKg() == null || pricingDto.getPricePerKg() <= 0)
            throw new UserException("PricePerKg must be greater than 0");

        if (pricingDto.getSaleType() == null)
            throw new UserException("SaleType must be specified");

        if (pricingDto.getSaleType() == SaleType.AUCTION) {
            if (pricingDto.getAuctionEndTime() == null) {
                throw new UserException("Auction End Time required");
            }
            if (pricingDto.getMinimumBidIncrement() == null ||
                    pricingDto.getMinimumBidIncrement() <= 0) {
                throw new UserException("Minimum Bid Increment required");
            }
            if (pricingDto.getAuctionEndTime().isBefore(LocalDateTime.now())) {
                throw new UserException("Auction End Time must be in the future");
            }
            logger.info("Auction Listing validated");
        } else if (pricingDto.getSaleType() == SaleType.FIXED) {
            logger.info("Fixed Price Listing validated");
        } else {
            throw new UserException("SaleType must be AUCTION or FIXED");
        }

        // PARTIAL ORDER VALIDATION
        if (pricingDto.getPurchaseType() == PurchaseType.PARTIAL_ORDER_ALLOWS) {
            if (pricingDto.getMinimumOrderQuantity() == null ||
                    pricingDto.getMinimumOrderQuantity() <= 0) {
                throw new UserException("Minimum Order Quantity required");
            }
            if (pricingDto.getMoqPricePerKg() == null ||
                    pricingDto.getMoqPricePerKg() <= 0) {
                throw new UserException("MOQ PricePerKg required");
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
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new UserException("Listing not found with id: " + id));
        return ListingMapper.toResponse(listing);
    }

    @Override
    public void deleteListing(Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new UserException("Listing not found with id: " + id));
        listingRepository.delete(listing);
    }

//    @Override
//    public ListingResponseDto previewListing(CreateListingRequest request) {
//        ProductListingDto productDto = request.getProduct();
//        QualityPricingListingDto pricingDto = request.getPricing();
//        QualityLocationListingDto locationDto = request.getLocation();
//
//        logger.debug("Previewing listing for product: {}", productDto.getCropName());
//
//        // Calculate Total Base Price
//        double totalBasePrice = pricingDto.getQuantity() * pricingDto.getPricePerKg();
//        pricingDto.setTotalBasePrice(totalBasePrice);
//
//        // Auto-calculate Minimum Bid Increment (2%) for auctions
//        if (pricingDto.getSaleType() == SaleType.AUCTION) {
//            double minIncrement = Math.ceil(totalBasePrice * 0.02);
//            pricingDto.setMinimumBidIncrement(minIncrement);
//        }
//
//        // Map to Entity (not saved)
//        Listing preview = ListingMapper.toEntity(productDto, pricingDto, locationDto,ListingImage,ListingCertificate);
//        return ListingMapper.toResponse(preview);
    //}

    public BidHistory placeBid(Long listingId, Double bidAmount, Long userId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new UserException("Listing not found with id: " + listingId));

        double totalBasePrice = listing.getTotalBasePrice();
        double minimumBidIncrement = listing.getMinimumBidIncrement();
        long totalBids = bidHistoryRepository.countByListing_ListingId(listingId);


        if (totalBids > 0) {
            double lastBidAmount = totalBasePrice + (totalBids * minimumBidIncrement);
            double exactRequired = lastBidAmount + minimumBidIncrement;

            if (bidAmount != exactRequired) {
                throw new UserException(
                        "Invalid bid! Current base is ₹" + lastBidAmount +
                                ". You must bid exactly ₹" + exactRequired +
                                " (increment is fixed at ₹" + minimumBidIncrement + ")"
                );
            }
        } else {
            double firstBidRequired = totalBasePrice + minimumBidIncrement;
            if (bidAmount != firstBidRequired) {
                throw new UserException(
                        "First bid must be exactly ₹" + firstBidRequired +
                                " (Base ₹" + totalBasePrice + " + fixed increment ₹" + minimumBidIncrement + ")"
                );
            }
        }

        BidHistory newBid = new BidHistory();
        newBid.setListing(listing);
        newBid.setAmountPerKg(BigDecimal.valueOf(bidAmount));
        newBid.setBidTime(LocalDateTime.now());

        BidHistory saved = bidHistoryRepository.save(newBid);
        logger.info("Bid saved — Round: {}, UserID: {}, Amount: ₹{}", totalBids + 1, userId, bidAmount);
        return saved;
    }

    @Override
    public SellerListingDto getListingTop5BidDetail(Long listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found with id: " + listingId));

        Double currentHighestBid = bidRepository
                .findTopByListingListingIdOrderByBidAmountDesc(listingId)
                .map(Bid::getBidAmount)
                .orElse(listing.getTotalBasePrice());

        List<BidResponseDto> top5Bids = bidRepository
                .findTop5ByListingListingIdOrderByBidAmountDesc(listingId)
                .stream()
                .map(bid -> new BidResponseDto(
                        bid.getBidId(),
                        bid.getBidAmount(),
                        bid.getBuyer().getFullName(),
                        bid.getBidTime(),
                        listing.getRemainingQuantity()
                ))
                .toList();

        long activeBidders = bidRepository.countActiveBidders(listingId);

        return new SellerListingDto(
                listing.getListingId(),
                listing.getCropName(),
                listing.getGrade(),
                listing.getQuantity(),
                listing.getUnit(),
                listing.getTotalBasePrice(),
                listing.getMinimumBidIncrement(),
                listing.getState(),
                listing.getDistrict(),
                listing.getAuctionEndTime(),
                currentHighestBid,
                activeBidders,
                top5Bids
        );
    }

    @Override
    public DashboardDto getSellerOverview() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long sellerId = seller.getUserId();
        Long activeListings = listingRepository.countBySellerUserIdAndStatus(sellerId, AuctionStatus.ACTIVE);
        Long pendingApprovals = listingRepository.countBySellerUserIdAndStatus(sellerId, AuctionStatus.PENDING);
        Long totalBidsReceived = bidRepository.countTotalBidsBySellerId(sellerId);
        Long totalRevenue = bidRepository.sumAmountByListingSellerId(sellerId);

        return new DashboardDto(activeListings, pendingApprovals, totalBidsReceived, totalRevenue);
    }

    @Override
    public ListingResponseDto updateListing(Long listingId,
                                            ProductListingDto productDto,
                                            QualityPricingListingDto pricingDto,
                                            QualityLocationListingDto locationDto) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new UserException("Listing not found with id: " + listingId));
        ListingMapper.updateEntity(listing, productDto, pricingDto, locationDto);
        Listing saved = listingRepository.save(listing);
        return ListingMapper.toResponse(saved);
    }
}