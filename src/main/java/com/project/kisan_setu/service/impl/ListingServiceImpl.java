package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.ListingResponseDto;
import com.project.kisan_setu.dto.ProductListingDto;
import com.project.kisan_setu.dto.QualityLocationListingDto;
import com.project.kisan_setu.dto.QualityPricingListingDto;
import com.project.kisan_setu.entity.BidHistory;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.ListingMapper;
import com.project.kisan_setu.repository.BidHistoryRepository;
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
    private static final Logger logger = LoggerFactory.getLogger(ListingServiceImpl.class);

    public ListingServiceImpl(ListingRepository listingRepository, BidHistoryRepository bidHistoryRepository) {
        this.listingRepository = listingRepository;
        this.bidHistoryRepository = bidHistoryRepository;
    }

    @Override
    public ListingResponseDto createListing(
            ProductListingDto productDto,
            QualityPricingListingDto pricingDto,
            QualityLocationListingDto locationDto) {
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
    }





