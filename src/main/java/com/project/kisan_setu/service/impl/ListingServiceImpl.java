package com.project.kisan_setu.service.impl;

<<<<<<< Updated upstream
import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.Bid;
=======
import com.project.kisan_setu.dto.ListingResponseDto;
import com.project.kisan_setu.dto.ProductListingDto;
import com.project.kisan_setu.dto.QualityLocationListingDto;
import com.project.kisan_setu.dto.QualityPricingListingDto;
>>>>>>> Stashed changes
import com.project.kisan_setu.entity.BidHistory;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.enums.SaleType;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.ListingMapper;
import com.project.kisan_setu.repository.BidHistoryRepository;
<<<<<<< Updated upstream
import com.project.kisan_setu.repository.BidRepository;
=======
>>>>>>> Stashed changes
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.service.ListingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final BidHistoryRepository bidHistoryRepository;
<<<<<<< Updated upstream
    private final BidRepository bidRepository;
    private static final Logger logger = LoggerFactory.getLogger(ListingServiceImpl.class);

    public ListingServiceImpl(ListingRepository listingRepository, BidHistoryRepository bidHistoryRepository, BidRepository bidRepository) {
        this.listingRepository = listingRepository;
        this.bidHistoryRepository = bidHistoryRepository;
        this.bidRepository = bidRepository;
=======
    private static final Logger logger = LoggerFactory.getLogger(ListingServiceImpl.class);

    public ListingServiceImpl(ListingRepository listingRepository, BidHistoryRepository bidHistoryRepository) {
        this.listingRepository = listingRepository;
        this.bidHistoryRepository = bidHistoryRepository;
>>>>>>> Stashed changes
    }
    @Override
    public ListingResponseDto createListing(
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto) {
<<<<<<< Updated upstream

        logger.info("Creating new listing for product: {}",
                productDto.getCropName());

        if (pricingDto.getQuantity() == null || pricingDto.getQuantity() <= 0) {
            throw new UserException("Quantity must be greater than 0");
        }

        if (pricingDto.getPricePerKg() == null || pricingDto.getPricePerKg() <= 0) {
            throw new UserException("PricePerKg must be greater than 0");
        }

        Double totalBasePrice = pricingDto.getQuantity() * pricingDto.getPricePerKg();

        pricingDto.setTotalBasePrice(totalBasePrice);
        logger.info("TotalBasePrice = {}", totalBasePrice);

        logger.info("Total Base Price Calculated: {}",
                totalBasePrice);

        // Purchase Type Logic
        if ("Partial Orders Allowed".equalsIgnoreCase(
                pricingDto.getPurchaseType())) {

            if (pricingDto.getMinimumOrderQuantity() == null ||
                    pricingDto.getMoqPricePerKg() == null) {

                throw new UserException(
                        "MOQ and MOQ price required for Partial Orders");
            }

            logger.info("Partial Orders Allowed - MOQ: {}, MOQ Price: {}",
                    pricingDto.getMinimumOrderQuantity(),
                    pricingDto.getMoqPricePerKg());

        } else {
            // Whole Lot Only
            pricingDto.setMinimumOrderQuantity(null);
            pricingDto.setMoqPricePerKg(null);

            logger.info("Whole Lot Only Purchase");
        }

        // Price Calculation
        if (pricingDto.getPricePerKg() == null) {
            throw new UserException("Price per Kg is required");
        }


        // Sale Type Logic
        if (pricingDto.getSaleType() == SaleType.AUCTION) {

            if (pricingDto.getMinimumBidIncrement() == null
                    || pricingDto.getMinimumBidIncrement() <= 0) {

                throw new UserException(
                        "Minimum bid increment must be greater than 0");
            }

            if (pricingDto.getAuctionEndTime() == null) {

                throw new UserException(
                        "Auction End Time required");
            }

            logger.info("Auction Listing → BasePrice: {}, Increment: {}, EndTime: {}",
                    totalBasePrice,
                    pricingDto.getMinimumBidIncrement(),
                    pricingDto.getAuctionEndTime());


        }

        else if (pricingDto.getSaleType() == SaleType.FIXED) {

            // Remove Auction fields
            pricingDto.setMinimumBidIncrement(null);
            pricingDto.setAuctionEndTime(null);

            logger.info("Fixed Price Listing → PricePerKg: {}, TotalPrice: {}",
                    pricingDto.getPricePerKg(),
                    totalBasePrice);

        }

        else {
            throw new UserException(
                    "SaleType must be FIXED or AUCTION");
        }

        // Save Listing
        Listing listing =
                ListingMapper.toEntity(
                        productDto,
                        pricingDto,
                        locationDto);

        Listing saved =
                listingRepository.save(listing);

        logger.info("Listing created successfully with ID: {}",
                saved.getListingId());
=======
        logger.info("Creating new listing for product: {}", productDto.getCropName());
        // Calculate total base price
        double totalBasePrice = pricingDto.getQuantity() * pricingDto.getPricePerKg();
        pricingDto.setTotalBasePrice(totalBasePrice);

        if (pricingDto.getMinimumBidIncrement() == null || pricingDto.getMinimumBidIncrement() <= 0) {
            throw new UserException("Minimum bid increment is required and must be greater than 0");
        }

        logger.info("Total Base Price: {}, Min Bid Increment: {}", totalBasePrice,pricingDto.getMinimumBidIncrement());

        Listing listing = ListingMapper.toEntity(productDto, pricingDto, locationDto);
        Listing saved = listingRepository.save(listing);


        logger.info("Listing created successfully with ID: {}", saved.getListingId());
>>>>>>> Stashed changes


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
                    logger.error("Can not Delete it.User not found with id: {}", id);
                    return new UserException("Listing not found with id: " + id);
                });

        listingRepository.delete(listing);
    }

    @Override
    public ListingResponseDto previewListing(
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto) {
        logger.debug("Previewing listing for product: {}", productDto.getCropName());


        double totalBasePrice = pricingDto.getQuantity() * pricingDto.getPricePerKg();
        pricingDto.setTotalBasePrice(totalBasePrice);
        pricingDto.setMinimumBidIncrement(Math.ceil(totalBasePrice * 0.02));
        Listing preview = ListingMapper.toEntity(productDto, pricingDto, locationDto);
        return ListingMapper.toResponse(preview);
    }

    public BidHistory placeBid(Long listingId, Double bidAmount, Long userId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new UserException("Listing not found with id: " + listingId));


        double totalBsePrice = listing.getTotalBasePrice();
        double minimumBidIncrement = listing.getMinimumBidIncrement();

        long totalBids = bidHistoryRepository.countByListing_ListingId(listingId);

        double minimumRequired = totalBsePrice + (totalBids + minimumBidIncrement);

        if (totalBids > 0) {
            double lastBidAmount = totalBsePrice + (totalBids - 1) * minimumBidIncrement;
            double exactRequired = lastBidAmount + minimumBidIncrement;

            if (bidAmount != exactRequired) {
                throw new UserException(
                        "Invalid bid! Current base is ₹" + lastBidAmount +
                                ". You must bid exactly ₹" + exactRequired +
                                " (increment is fixed at ₹" + minimumBidIncrement + ")"
                );
            }
        } else {
            // First bid must be exactly basePrice + increment
            if (bidAmount != minimumRequired) {
                throw new UserException(
                        "First bid must be exactly ₹" + minimumRequired +
                                " (Base ₹" + totalBsePrice + " + fixed increment ₹" + minimumBidIncrement + ")"
                );
            }
        }


            BidHistory newBid = new BidHistory();
            newBid.setListing(listing);
            newBid.setAmountPerKg(BigDecimal.valueOf(bidAmount));
            newBid.setBidTime(LocalDateTime.now());

            BidHistory saved = bidHistoryRepository.save(newBid);
            logger.info("Bid saved — Round: {}, UserID: {}, Amount: ₹{}",
                    totalBids + 1, userId, bidAmount);

            return saved;


        }
<<<<<<< Updated upstream

    @Override
    public SellerListingDto getListingTop5BidDetail(Long listingId) {
        Listing listing=listingRepository.findById(listingId).orElseThrow(()->
                new RuntimeException("listing not found with id :"+listingId));
        //fetch currentHighestBid
        Double currentHighestBid = bidRepository.
                findTopByListingListingIdOrderByBidAmountDesc(listingId)
                .map(Bid::getBidAmount)
                .orElse(listing.getTotalBasePrice());

        List<BidResponseDto> top5Bids =
                bidRepository.findTop5ByListingListingIdOrderByBidAmountDesc(listingId)
                        .stream()
                        .map((Bid bid) -> new BidResponseDto(
                                bid.getBidId(),
                                bid.getBidAmount(),
                                bid.getBuyer().getFullName(),
                                bid.getBidTime()
                        ))
                        .toList();

        //Count Active Bidders
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
}
=======
    }
>>>>>>> Stashed changes





