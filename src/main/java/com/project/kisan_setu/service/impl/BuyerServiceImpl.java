package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.embedded.ListingImage;
import com.project.kisan_setu.entity.*;
import com.project.kisan_setu.enums.BidStatus;
import com.project.kisan_setu.enums.InquiryStatus;
import com.project.kisan_setu.enums.PurchaseType;
import com.project.kisan_setu.enums.SaleType;
import com.project.kisan_setu.mapper.BuyingRequirementMapper;
import com.project.kisan_setu.repository.*;
import com.project.kisan_setu.service.BuyerService;
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
    private final BuyerInquiryRepository buyerInquiryRepository;
    private final BuyingRequirementRepository buyingRequirementRepository;

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

        return listingRepository.findBySaleTypeAndSellerUserIdNot(SaleType.AUCTION, userId)
                .stream()
                .map(this::toBuyerListingResponse)
                .toList();
    }

    @Override
    public BuyerListingResponseDto getAuctionListingDetail(Long listingId) {
        Listing listing = validatorMethods.validateExists(listingId);
        if (listing.getSaleType() != SaleType.AUCTION) {
            throw new RuntimeException("Listing is not an auction");
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

        // Initialize remaining quantity if null
        if (listing.getRemainingQuantity() == null) {
            listing.setRemainingQuantity(listing.getQuantity());
            listingRepository.save(listing);
        }

        // Seller cannot buy own listing
        if (listing.getSeller().getUserId().equals(buyer.getUserId())) {
            throw new RuntimeException("Seller cannot buy own listing");
        }


        if (listing.getSaleType() == SaleType.FIXED) {

            if (dto.getQuantity() == null) {
                throw new RuntimeException("Quantity required");
            }

            if (listing.getRemainingQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Listing sold out");
            }

            if (listing.getRemainingQuantity().compareTo(dto.getQuantity()) < 0) {
                throw new RuntimeException("Not enough quantity available");
            }

            // LOT
            if (listing.getPurchaseType() == PurchaseType.WHOLE_LOT_ONLY) {
                if (dto.getQuantity().compareTo(listing.getRemainingQuantity()) != 0) {
                    throw new RuntimeException(
                            "You must buy full quantity: " + listing.getRemainingQuantity()
                    );
                }
            }

            // PARTIAL ORDER
            if (listing.getPurchaseType() == PurchaseType.PARTIAL_ORDER_ALLOWS) {
                if (dto.getQuantity()
                        .compareTo(listing.getMinimumOrderQuantity()) < 0) {

                    throw new RuntimeException(
                            "Minimum order quantity is "
                                    + listing.getMinimumOrderQuantity());
                }
            }

            // PRICE SELECTION
            BigDecimal pricePerKg;

            if (listing.getPurchaseType() == PurchaseType.PARTIAL_ORDER_ALLOWS &&
                    dto.getQuantity().compareTo(listing.getRemainingQuantity()) < 0) {

                pricePerKg = listing.getMoqPricePerKg();
            } else {
                pricePerKg = listing.getPricePerKg();
            }

            BigDecimal totalBasePrice =
                    pricePerKg.multiply(dto.getQuantity());

            listing.setRemainingQuantity(
                    listing.getRemainingQuantity()
                            .subtract(dto.getQuantity())
            );

            listingRepository.save(listing);

            BuyerInquiry inquiry = new BuyerInquiry();
            inquiry.setBuyer(buyer);
            inquiry.setListing(listing);
            inquiry.setQuantityRequested(dto.getQuantity());
            inquiry.setStatus(InquiryStatus.PENDING);
            inquiry.setInquiryTime(LocalDateTime.now());
            inquiry.setRemainingQuantity(listing.getRemainingQuantity());

            buyerInquiryRepository.save(inquiry);

            return new InquiryResponseDto(
                    inquiry.getInquiryId(),
                    listing.getListingId(),
                    inquiry.getBuyer().getFullName(),
                    inquiry.getListing().getCrop().getCropName(),
                    inquiry.getQuantityRequested(),
                    inquiry.getInquiryTime(),
                    inquiry.getStatus(),
                    inquiry.getRemainingQuantity()
            );
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

        // PARTIAL ORDER AUCTION
        if (listing.getPurchaseType() == PurchaseType.PARTIAL_ORDER_ALLOWS) {

            if (dto.getQuantity()
                    .compareTo(listing.getMinimumOrderQuantity()) < 0) {

                throw new RuntimeException(
                        "Minimum order quantity is "
                                + listing.getMinimumOrderQuantity());
            }

            if (dto.getQuantity()
                    .compareTo(listing.getRemainingQuantity()) > 0) {

                throw new RuntimeException("Quantity exceeds available stock");
            }

            BigDecimal totalPrice =
                    listing.getMoqPricePerKg()
                            .multiply(dto.getQuantity());

            listing.setRemainingQuantity(
                    listing.getRemainingQuantity()
                            .subtract(dto.getQuantity())
            );

            listingRepository.save(listing);

            return new BidResponseDto(
                    buyer.getUserId(),
                    totalPrice,
                    buyer.getFullName(),
                    LocalDateTime.now(),
                    BidStatus.NEW

            );
        }

        //  LOT AUCTION
        if (listing.getPurchaseType() == PurchaseType.WHOLE_LOT_ONLY) {

            if (dto.getBuyerAmount() == null) {
                throw new RuntimeException("Bid amount required");
            }

            BigDecimal basePrice = listing.getTotalBasePrice();
            if (basePrice == null) {
                basePrice = listing.getPricePerKg()
                        .multiply(listing.getQuantity());
            }

            Optional<Bid> highestBidOpt = bidRepository
                    .findTopByListingListingIdOrderByBuyerAmountDesc(listingId);

            BigDecimal expectedNextBid;
            if (highestBidOpt.isPresent()) {
                if (listing.getMinimumBidIncrement() == null) {
                    throw new RuntimeException("Minimum bid increment not set");
                }
                BigDecimal currentHighest = highestBidOpt.get().getBuyerAmount();
                expectedNextBid = currentHighest.add(listing.getMinimumBidIncrement());
            } else {
                expectedNextBid = basePrice;
            }

            if (dto.getBuyerAmount().compareTo(expectedNextBid) != 0) {
                throw new RuntimeException(
                        "Bid must be exactly "
                                + expectedNextBid
                );
            }

            Bid bid = new Bid();
            bid.setBuyerAmount(dto.getBuyerAmount());
            bid.setBidTime(LocalDateTime.now());
            bid.setBidStatus(BidStatus.NEW);
            bid.setListing(listing);
            bid.setBuyer(buyer);

            bidRepository.save(bid);

            return new BidResponseDto(
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
        BigDecimal currentHighest = bidRepository
                .findTopByListingListingIdOrderByBuyerAmountDesc(listing.getListingId())
                .map(Bid::getBuyerAmount)
                .orElse(listing.getPricePerKg());

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
                listing.getTotalBasePrice(),
                listing.getPricePerKg(),
                listing.getPurchaseType(),
                listing.getDistrict() != null ? listing.getDistrict().getName() : null,
                listing.getAuctionEndTime(),
                currentHighest,
                images
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
}
