package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.RequestDto.BidHistoryDto;
import com.project.kisan_setu.dto.RequestDto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.RequestDto.PlaceBidRequestDto;
import com.project.kisan_setu.dto.ResponseDto.*;
import com.project.kisan_setu.embedded.ListingImage;
import com.project.kisan_setu.entity.*;
import com.project.kisan_setu.enums.*;
import com.project.kisan_setu.mapper.BuyingRequirementMapper;
import com.project.kisan_setu.mapper.ListingMapper;
import com.project.kisan_setu.repository.*;
import com.project.kisan_setu.service.BuyerService;
import com.project.kisan_setu.service.NotificationService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class BuyerServiceImpl implements BuyerService {
    private final ListingRepository listingRepository;
    private final BidRepository bidRepository;
    private final ValidatorMethods validatorMethods;
    private final BuyingRequirementRepository buyingRequirementRepository;
    private final NotificationService notificationService;

    // Post Requirement
    @Override
    public BuyingRequirementResponseDto postRequirement(BuyingRequirementRequestDto dto) {

       Long userId = validatorMethods.getCurrentUserId();
        User buyer = validatorMethods.validateUserById(userId);
        CropMaster crop = validatorMethods.validateCrop(Long.valueOf(dto.getCropId()));
        UnitMaster unit = validatorMethods.validateUnit(Long.valueOf(dto.getUnitId()));
        StateMaster state = validatorMethods.validateState(Long.valueOf(dto.getStateId()));
        DistrictMaster district = validatorMethods.validateDistrict(Long.valueOf(dto.getDistrictId()));

        BuyingRequirement requirement=
                BuyingRequirementMapper.toEntity(dto, buyer,crop,unit,state,district);

        requirement.setState(state);
        requirement.setDistrict(district);

        BuyingRequirement saved =
                buyingRequirementRepository.save(requirement);

        return BuyingRequirementMapper.toDto(saved);
    }


    // Get Active Auction Listings
    @Override
    public List<BuyerListingResponseDto> getActiveAuctionListings(Long userId) {

        return listingRepository.findBySaleTypeAndSellerUserIdNotAndStatus(SaleType.AUCTION, userId, AuctionStatus.ACTIVE)
                .stream()
                .map(this::toBuyerListingResponse)
                .toList();
    }

    @Override
    public List<BuyerListingResponseDto> getActiveFixedListings(Long userId) {
        return listingRepository.findBySaleTypeAndSellerUserIdNotAndStatus(SaleType.FIXED, userId,AuctionStatus.ACTIVE)
                .stream()
                .map(this::toBuyerListingResponse)
                .toList();
    }

    @Override
    public BuyerListingResponseDto getAuctionListingDetail(Long listingId) {
        Listing listing = validatorMethods.validateExists(listingId);
        if (listing.getSaleType() != SaleType.AUCTION) {
            throw new RuntimeException("Listing is not auction");
        }
        return toBuyerListingResponse(listing);
    }

    @Override
    public BuyerListingResponseDto getFixedListingDetail(Long listingId){
        Listing listing = validatorMethods.validateExists(listingId);
        if (listing.getSaleType() != SaleType.FIXED){
            throw new RuntimeException("Listing is not fixed");
        }
        return toBuyerListingResponse(listing);
    }

    // Place Bid

    @Override
    @Transactional
    public Object placeBid(Long listingId, PlaceBidRequestDto dto) {

        Long userId = validatorMethods.getCurrentUserId();
        Listing listing = listingRepository.findByIdForUpdate(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        User buyer = validatorMethods.validateUserById(userId);

        // Seller cannot buy own listing
        if (listing.getSeller().getUserId().equals(buyer.getUserId())) {
            throw new RuntimeException("Seller cannot buy own listing");
        }

        if (listing.getSaleType() != SaleType.AUCTION) {
            throw new RuntimeException("Invalid sale type");
        }

        if (listing.getAuctionEndTime() == null) {
            throw new RuntimeException("Auction end time not set");
        }

        if (LocalDateTime.now().isAfter(listing.getAuctionEndTime())) {
            throw new RuntimeException("Auction Time Ended");
        }

        Optional<Bid> latestBidOpt =
                bidRepository.findTopByListingListingIdOrderByBidIdDesc(listingId);
        if (latestBidOpt.isPresent()) {
            Bid latestBid = latestBidOpt.get();
            if (latestBid.getBuyer() != null
                    && latestBid.getBuyer().getUserId().equals(buyer.getUserId())) {
                throw new RuntimeException(
                        "You must wait for another buyer to place a bid before bidding again."
                );
            }
        }

        //  LOT AUCTION
        if (listing.getPurchaseType() == PurchaseType.WHOLE_LOT_ONLY) {

            if (dto.getBuyerAmount() == null) {
                throw new RuntimeException("Bid amount required");
            }

            BigDecimal basePrice = listing.getTotalBasePrice();
            if (basePrice == null && listing.getPricePerKg() != null && listing.getQuantity() != null) {
                basePrice = listing.getPricePerKg()
                        .multiply(listing.getQuantity());
            }
            if (basePrice == null) {
                throw new RuntimeException("Base price not set");
            }

            Optional<Bid> highestBidOpt = bidRepository
                    .findTopByListingListingIdOrderByBuyerAmountDesc(listingId);

            BigDecimal expectedNextBid;

            if (highestBidOpt.isEmpty()) {
                if (listing.getMinimumBidIncrement() == null) {
                    throw new RuntimeException("Minimum bid increment not set");
                }
                // First bid must be base price + increment
                expectedNextBid = basePrice.add(listing.getMinimumBidIncrement());
            } else {
                // Subsequent bids
                if (listing.getMinimumBidIncrement() == null) {
                    throw new RuntimeException("Minimum bid increment not set");
                }

                BigDecimal currentHighest = highestBidOpt.get().getBuyerAmount();
                expectedNextBid = currentHighest.add(listing.getMinimumBidIncrement());
            }

            if (dto.getBuyerAmount().compareTo(expectedNextBid) != 0) {
                throw new RuntimeException(
                        "Bid must be exactly " + expectedNextBid
                );
            }

            Bid bid = new Bid();
            bid.setBuyerAmount(dto.getBuyerAmount());
            bid.setBidTime(LocalDateTime.now());
            bid.setBidStatus(BidStatus.NEW);
            bid.setListing(listing);
            bid.setBuyer(buyer);

            bidRepository.save(bid);
            notificationService.createNotification(listing.getSeller(),
                    "New bid placed on your listing",
                    NotificationStatus.BID_PLACED,listing,bid,null);

            return new BidResponseDto(
                    bid.getBidId(),
                    bid.getBuyer().getUserId(),
                    bid.getBuyerAmount(),
                    buyer.getFullName(),
                    bid.getBidTime(),
                    BidStatus.NEW

            );
        }

        throw new RuntimeException("Invalid purchase type");
    }

    private BuyerListingResponseDto toBuyerListingResponse(Listing listing) {
        BigDecimal basePrice = listing.getTotalBasePrice();
        if (basePrice == null && listing.getPricePerKg() != null && listing.getQuantity() != null) {
            basePrice = listing.getPricePerKg()
                    .multiply(listing.getQuantity());
        }

        BigDecimal resolvedBasePrice = basePrice != null ? basePrice : listing.getPricePerKg();
        BigDecimal noBidHighest = listing.getPurchaseType() == PurchaseType.WHOLE_LOT_ONLY
                ? resolvedBasePrice
                : listing.getPricePerKg();

        BigDecimal currentHighest = bidRepository
                .findTopByListingListingIdOrderByBuyerAmountDesc(listing.getListingId())
                .map(Bid::getBuyerAmount)
                .orElse(noBidHighest);

        List<ProductImageResponseDto> images = listing.getImages() == null
                ? List.of()
                : listing.getImages().stream()
                .map(this::toImageResponse)
                .collect(Collectors.toList());

        return new BuyerListingResponseDto(
                listing.getListingId(),
                listing.getCrop().getCropName(),
                listing.getVariety(),
                listing.getState() != null ? listing.getState().getName() : null,
                listing.getQuantity(),
                listing.getUnit().getUnitName(),
                listing.getPackaging() != null ? listing.getPackaging().getPackagingType() : null,
                listing.getSaleType(),
                listing.getStorage() != null ? listing.getStorage().getStorageType() : null,
                listing.getHarvestDate(),
                listing.getMinimumBidIncrement(),
                listing.getGrade(),
                resolvedBasePrice,
                listing.getPricePerKg(),
                listing.getPurchaseType(),
                listing.getDistrict() != null ? listing.getDistrict().getName() : null,
                listing.getAuctionEndTime(),
                currentHighest,
                images,
                listing.getMinimumOrderQuantity()
        );
    }

    private ProductImageResponseDto toImageResponse(ListingImage image) {
        ProductImageResponseDto dto = new ProductImageResponseDto();
        dto.setFileName(image.getFileName());
        dto.setFilePath(image.getFilePath());
        dto.setFileType(image.getFileType());
        dto.setIsPrimary(image.getIsPrimary());
        return dto;
    }
    @Override
    public List<BidHistoryDto> getBidHistory(Long listingId) {
        List<Bid> bids =  bidRepository.findByListingListingIdOrderByBuyerAmountDesc(listingId);
        return IntStream.range(0,bids.size()).mapToObj(i -> {
            Bid bid = bids.get(i);
            BidHistoryDto dto = new BidHistoryDto();
            dto.setBidId(bid.getBidId());
            dto.setBuyerAmount(bid.getBuyerAmount());
            dto.setBidTime(bid.getBidTime());
            dto.setBidderName(bid.getBuyer().getFullName());

            dto.setBidHistoryStatus(i==0 ? "LEADING" : "OUTBID");
            return dto;
        }).toList();
    }

    @Override
    public ListingSummaryResponseDto getListingSummary(Long listingId) {
        Listing listing = validatorMethods.validateExists(listingId);
        ListingSummaryResponseDto dto = ListingMapper.toSummaryResponse(listing);
        dto.setCurrentHighestBid(resolveCurrentHighestBid(listing));
        return dto;
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
