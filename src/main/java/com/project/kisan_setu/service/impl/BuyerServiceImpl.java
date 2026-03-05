package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.*;
import com.project.kisan_setu.enums.InquiryStatus;
import com.project.kisan_setu.enums.PurchaseType;
import com.project.kisan_setu.enums.SaleType;
import com.project.kisan_setu.mapper.BuyingRequirementMapper;
import com.project.kisan_setu.repository.*;
import com.project.kisan_setu.service.BuyerService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuyerServiceImpl implements BuyerService {

    private final BuyingRequirementRepository requirementRepository;
    private final ListingRepository listingRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ValidatorMethods validatorMethods;
    private final BuyerInquiryRepository buyerInquiryRepository;
    private final BuyingRequirementRepository buyingRequirementRepository;


    // Post Requirement
    @Override
    public BuyingRequirementResponseDto postRequirement(BuyingRequirementRequestDto dto) {

        String email = validatorMethods.getCurrentUserEmail();
        User buyer = validatorMethods.validateUserByEmail(email);

        BuyingRequirement requirement=
                BuyingRequirementMapper.toEntity(dto, buyer);
        BuyingRequirement saved =
                buyingRequirementRepository.save(requirement);

        return BuyingRequirementMapper.toDto(saved);
    }


    // Get Active Auction Listings
    @Override
    public List<BuyerListingResponseDto> getActiveAuctionListings() {

        return listingRepository.findBySaleType(SaleType.AUCTION)
                .stream()
                .map(listing -> {

                    BigDecimal currentHighest = bidRepository
                            .findTopByListingListingIdOrderByBuyerAmountDesc(
                                    listing.getListingId())
                            .map(Bid::getBuyerAmount)
                            .orElse(listing.getPricePerKg());

                    return new BuyerListingResponseDto(
                            listing.getListingId(),
                            listing.getCropName(),
                            listing.getGrade(),
                            listing.getTotalBasePrice(),
                            currentHighest,
                            listing.getDistrict(),
                            listing.getAuctionEndTime()
                    );
                })
                .toList();
    }

    // Place Bid

    @Override
    public Object placeBid(Long listingId, PlaceBidRequestDto dto) {

        Listing listing = validatorMethods.validateExists(listingId);
        String email = validatorMethods.getCurrentUserEmail();
        User buyer = validatorMethods.validateUserByEmail(email);

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

            // WHOLE LOT
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

            buyerInquiryRepository.save(inquiry);

            return new InquiryResponseDto(
                    inquiry.getInquiryId(),
                    listing.getListingId(),
                    inquiry.getBuyer().getFullName(),
                    inquiry.getListing().getCropName(),
                    inquiry.getQuantityRequested(),
                    inquiry.getInquiryTime(),
                    inquiry.getStatus()
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
                    listing.getRemainingQuantity()
            );
        }

        // WHOLE LOT AUCTION
        if (listing.getPurchaseType() == PurchaseType.WHOLE_LOT_ONLY) {

            BigDecimal basePrice =
                    listing.getPricePerKg()
                            .multiply(listing.getQuantity());

            BigDecimal currentHighest = bidRepository
                    .findTopByListingListingIdOrderByBuyerAmountDesc(listingId)
                    .map(Bid::getBuyerAmount)
                    .orElse(basePrice);

            if (listing.getMinimumBidIncrement() == null) {
                throw new RuntimeException("Minimum bid increment not set");
            }

            BigDecimal expectedNextBid =
                    currentHighest.add(listing.getMinimumBidIncrement());

            if (dto.getBuyerAmount()
                    .compareTo(expectedNextBid) != 0) {

                throw new RuntimeException(
                        "Bid must be exactly last bid + minimum increment: "
                                + expectedNextBid
                );
            }

            Bid bid = new Bid();
            bid.setBuyerAmount(dto.getBuyerAmount());
            bid.setBidTime(LocalDateTime.now());
            bid.setListing(listing);
            bid.setBuyer(buyer);

            bidRepository.save(bid);

            return new BidResponseDto(
                    bid.getBuyer().getUserId(),
                    bid.getBuyerAmount(),
                    buyer.getFullName(),
                    bid.getBidTime(),
                    listing.getRemainingQuantity()
            );
        }

        throw new RuntimeException("Invalid purchase type");
    }


        @Override
        public Object getBidHistory (Long listingId){
            return null;
        }
    }




//    // Bid History
//    @Override
//    public List<BidResponseDto> getBidHistory(Long listingId) {
//
//        return bidRepository
//                .findByListingListingIdOrderBybuyerAmountDesc(listingId)
//                .stream()
//                .map(bid -> new BidResponseDto(
//                        bid.getBidId(),
//                        bid.getbuyerAmount(),
//                        bid.getBuyer().getFullName(),
//                        bid.getBidTime()
//                ))
//                .toList();
//
//    }
