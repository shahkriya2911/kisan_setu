package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.*;
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
import com.project.kisan_setu.service.ListingService;
import com.project.kisan_setu.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final BidHistoryRepository bidHistoryRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final NotificationService notificationService;
    private static final Logger logger = LoggerFactory.getLogger(ListingServiceImpl.class);

    public ListingServiceImpl(ListingRepository listingRepository,  BidHistoryRepository bidHistoryRepository, UserRepository userRepository, BidRepository bidRepository, NotificationService notificationService) {
        this.listingRepository = listingRepository;
        this.bidHistoryRepository = bidHistoryRepository;

        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
        this.notificationService = notificationService;

    }

    @Override
    public ListingResponseDto createListing(CreateListingRequest request) {

        ProductListingDto productDto = request.getProduct();
        QualityPricingListingDto pricingDto = request.getPricing();
        QualityLocationListingDto locationDto = request.getLocation();

        logger.info("Creating listing for product: {}", productDto.getCropName());

        // BASIC VALIDATION


        if (pricingDto.getQuantity() == null || pricingDto.getQuantity() <= 0) {
            throw new UserException("Quantity must be greater than 0");
        }

        if (pricingDto.getPricePerKg() == null || pricingDto.getPricePerKg() <= 0) {
            throw new UserException("PricePerKg must be greater than 0");
        }

        // SALE TYPE VALIDATION

        if (pricingDto.getSaleType() == SaleType.AUCTION) {

            if (pricingDto.getAuctionEndTime() == null) {
                throw new UserException("Auction End Time required");
            }

            if (pricingDto.getMinimumBidIncrement() == null ||
                    pricingDto.getMinimumBidIncrement() <= 0) {
                throw new UserException("Minimum Bid Increment required");
            }

            if (pricingDto.getAuctionEndTime().isBefore(LocalDateTime.now())) {
                throw new UserException("Auction End Time must be future");
            }

            logger.info("Auction Listing Created");
        }
        else if (pricingDto.getSaleType() == SaleType.FIXED) {

            logger.info("Fixed Price Listing Created");
        }
        else {
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

            logger.info("Partial Order Listing");
        }

        // MAP ENTITY

        Listing listing = ListingMapper.toEntity(
                productDto,
                pricingDto,
                locationDto
        );

        // AUTHENTICATED SELLER

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        listing.setSeller(seller);
        listing.setStatus(AuctionStatus.ACTIVE);

        // Total Base Price
        listing.setTotalBasePrice(
                pricingDto.getPricePerKg() * pricingDto.getQuantity()
        );

        // Remaining Quantity
        listing.setRemainingQuantity(pricingDto.getRemainingQuantity());

        Listing saved = listingRepository.save(listing);

        notificationService.createNotification(
                seller.getUserId(),
                "Crop Created Successfully"
        );

        logger.info("Listing Created Successfully ID: {}", saved.getListingId());

        return ListingMapper.toResponse(saved);
    }

    @Override
    public List<ListingResponseDto> getAllListings() {
        logger.info("Fetching all listings");
        return listingRepository.findAll()
                .stream()
                .map(ListingMapper::toResponse)
                .toList();
    }

    @Override
    public ListingResponseDto getListingById(Long id) {
        logger.info("Fetching listing with ID: {}", id);

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Listing not found with ID: {}", id);
                    return new UserException("Listing not found with id: " + id);
                });

        return ListingMapper.toResponse(listing);
    }

    @Override
    public void deleteListing(Long id) {
        logger.info("Deleting listing with ID: {}", id);

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Cannot delete. Listing not found with id: {}", id);
                    return new UserException("Listing not found with id: " + id);
                });

        listingRepository.delete(listing);
    }

    @Override
    public ListingResponseDto previewListing(CreateListingRequest request) {

        ProductListingDto productDto = request.getProduct();
        QualityPricingListingDto pricingDto = request.getPricing();
        QualityLocationListingDto locationDto = request.getLocation();

        logger.debug("Previewing listing for product: {}", productDto.getCropName());

        // 🔹 Calculate Total Base Price
        double totalBasePrice =
                pricingDto.getQuantity() * pricingDto.getPricePerKg();

        pricingDto.setTotalBasePrice(totalBasePrice);

        // 🔹 Auto calculate Minimum Bid Increment (2%)
        if (pricingDto.getSaleType() == SaleType.AUCTION) {
            double minIncrement = Math.ceil(totalBasePrice * 0.02);
            pricingDto.setMinimumBidIncrement(minIncrement);
        }

        // 🔹 Map to Entity (Not Saved)
        Listing preview =
                ListingMapper.toEntity(productDto, pricingDto, locationDto);

        return ListingMapper.toResponse(preview);
    }

    public BidHistory placeBid(Long listingId, Double bidAmount, Long userId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new UserException("Listing not found with id: " + listingId));

        double totalBasePrice = listing.getTotalBasePrice();
        double minimumBidIncrement = listing.getMinimumBidIncrement();
        long totalBids = bidHistoryRepository.countByListing_ListingId(listingId);
        double minimumRequired = totalBasePrice + (totalBids + minimumBidIncrement);

        if (totalBids > 0) {
            double lastBidAmount = totalBasePrice + (totalBids - 1) * minimumBidIncrement;
            double exactRequired = lastBidAmount + minimumBidIncrement;

            if (bidAmount != exactRequired) {
                throw new UserException(
                        "Invalid bid! Current base is ₹" + lastBidAmount +
                                ". You must bid exactly ₹" + exactRequired +
                                " (increment is fixed at ₹" + minimumBidIncrement + ")"
                );
            }
        } else {
            if (bidAmount != minimumRequired) {
                throw new UserException(
                        "First bid must be exactly ₹" + minimumRequired +
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

        // Fetch current highest bid
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
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();

        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long sellerId = seller.getUserId();

        Long activeListings = listingRepository.countBySellerUserIdAndStatus(sellerId,AuctionStatus.ACTIVE);
        Long pendingApprovals = listingRepository.countBySellerUserIdAndStatus(sellerId,AuctionStatus.PENDING);
        Long totalBidsReceived = bidRepository.countTotalBidsBySellerId(sellerId);
        Long totalRevenue = bidRepository.sumAmountByListingSellerId(sellerId);

        return new DashboardDto(activeListings, pendingApprovals, totalBidsReceived, totalRevenue);


    }


}