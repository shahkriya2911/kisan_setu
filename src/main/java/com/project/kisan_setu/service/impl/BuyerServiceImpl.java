package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.*;
import com.project.kisan_setu.enums.PurchaseType;
import com.project.kisan_setu.enums.SaleType;
import com.project.kisan_setu.mapper.BuyingRequirementMapper;
import com.project.kisan_setu.repository.*;
import com.project.kisan_setu.service.BuyerService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BuyerServiceImpl implements BuyerService {

    private final BuyingRequirementRepository requirementRepository;
    private final ListingRepository listingRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public BuyerServiceImpl(
            BuyingRequirementRepository requirementRepository,
            ListingRepository listingRepository,
            BidRepository bidRepository,
            UserRepository userRepository, OrderRepository orderRepository) {
        this.requirementRepository = requirementRepository;
        this.listingRepository = listingRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    // Post Requirement
    @Override
    public BuyingRequirementResponseDto postRequirement(
            Long buyerId,
            BuyingRequirementRequestDto dto) {

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BuyingRequirement br =
                BuyingRequirementMapper.toEntity(dto, buyer);

        requirementRepository.save(br);

        return BuyingRequirementMapper.toDto(br);
    }

    // Get Active Auction Listings
    @Override
    public List<BuyerListingResponseDto> getActiveAuctionListings() {

        return listingRepository.findBySaleType(SaleType.AUCTION)
                .stream()
                .map(listing -> {

                    Double currentHighest = bidRepository
                            .findTopByListingListingIdOrderByBidAmountDesc(
                                    listing.getListingId())
                            .map(Bid::getBidAmount)
                            .orElse(listing.getPricePerKg());

                    return new BuyerListingResponseDto(
                            listing.getListingId(),
                            listing.getCropName(),
                            listing.getGrade(),
                            listing.getPricePerKg(),
                            currentHighest,
                            listing.getDistrict(),
                            listing.getAuctionEndTime()
                    );
                })
                .toList();
    }

    // Place Bid

    @Override
    public Object placeBid(Long listingId,
                           PlaceBidRequestDto dto) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = auth.getName();

        User buyer = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // SELLER CANNOT BUY OWN LISTING
        if (listing.getSeller().getUserId()
                .equals(buyer.getUserId())) {
            throw new RuntimeException("Seller cannot buy own listing");
        }

        //FIXED PRICE LOGIC

        if (listing.getSaleType() == SaleType.FIXED) {

            if (dto.getQuantity() == null) {
                throw new RuntimeException("Quantity required");
            }

            if (listing.getRemainingQuantity() <= 0) {
                throw new RuntimeException("Listing sold out");
            }

            if (listing.getRemainingQuantity() < dto.getQuantity()) {
                throw new RuntimeException("Not enough quantity available");
            }
            //WHOLE LOT
            if (listing.getPurchaseType() == PurchaseType.WHOLE_LOT_ONLY) {

                if (!dto.getQuantity()
                        .equals(listing.getRemainingQuantity())) {

                    throw new RuntimeException(
                            "You must buy full quantity: "
                                    + listing.getRemainingQuantity());
                }

            }

            //PARTIAL ORDER

            if (listing.getPurchaseType() == PurchaseType.PARTIAL_ORDER_ALLOWS) {

                if (dto.getQuantity() < listing.getMinimumOrderQuantity()) {

                    throw new RuntimeException(
                            "Minimum order quantity is "
                                    + listing.getMinimumOrderQuantity());
                }
            }

            // PRICE SELECTION

            Double pricePerKg;

            if (listing.getPurchaseType() == PurchaseType.PARTIAL_ORDER_ALLOWS &&
                    dto.getQuantity() < listing.getRemainingQuantity()) {

                pricePerKg = listing.getMoqPricePerKg();
            } else {
                pricePerKg = listing.getPricePerKg();
            }

            Double totalBasePrice = listing.getMoqPricePerKg() * dto.getQuantity();
            listing.setRemainingQuantity(
                    listing.getRemainingQuantity() - dto.getQuantity()
            );


            //SAVE ORDER
            Order order = new Order();
            order.setBuyer(buyer);
            order.setListing(listing);
            order.setQuantity(dto.getQuantity());
            order.setPricePerKg(pricePerKg);
            order.setTotalBasePrice(totalBasePrice);
            order.setOrderTime(LocalDateTime.now());

            orderRepository.save(order);

            // UPDATE REMAINING QUANTITY

            listingRepository.save(listing);

            return new OrderResponseDto(
                    order.getOrderId(),
                    buyer.getFullName(),
                    listing.getCropName(),
                    order.getQuantity(),
                    order.getPricePerKg(),
                    order.getTotalBasePrice(),
                    order.getOrderTime(),
                    order.getListing().getRemainingQuantity()
            );
        }

       // Must be auction
        if (listing.getSaleType() != SaleType.AUCTION) {
            throw new RuntimeException("Invalid sale type");
        }

       // Auction time check
        if (LocalDateTime.now().isAfter(listing.getAuctionEndTime())) {
            throw new RuntimeException("Auction Time Ended");
        }

        // Seller cannot bid
        if (listing.getSeller().getUserId().equals(buyer.getUserId())) {
            throw new RuntimeException("Seller cannot bid on own listing");
        }


         // PARTIAL ORDER (FIXED PRICE)

        if (listing.getPurchaseType() == PurchaseType.PARTIAL_ORDER_ALLOWS) {

            if (dto.getQuantity() < listing.getMinimumOrderQuantity()) {
                throw new RuntimeException(
                        "Minimum order quantity is " + listing.getMinimumOrderQuantity()
                );
            }

            if (dto.getQuantity() > listing.getRemainingQuantity()) {
                throw new RuntimeException("Quantity exceeds available stock");
            }

            Double totalPrice =
                    listing.getMoqPricePerKg() * dto.getQuantity();

            listing.setRemainingQuantity(
                    listing.getRemainingQuantity() - dto.getQuantity()
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

        else if (listing.getPurchaseType() == PurchaseType.WHOLE_LOT_ONLY) {

            Double currentHighest = bidRepository
                    .findTopByListingListingIdOrderByBidAmountDesc(listingId)
                    .map(Bid::getBidAmount)
                    .orElse(listing.getPricePerKg() * listing.getQuantity());

            double expectedNextBid =
                    currentHighest + listing.getMinimumBidIncrement();

            if (Double.compare(dto.getBidAmount(), expectedNextBid) != 0) {
                throw new RuntimeException(
                        "Bid must be exactly last bid + minimum increment: "
                                + expectedNextBid
                );
            }

            Bid bid = new Bid();
            bid.setBidAmount(dto.getBidAmount());
            bid.setBidTime(LocalDateTime.now());
            bid.setListing(listing);
            bid.setBuyer(buyer);

            bidRepository.save(bid);

            return new BidResponseDto(
                    bid.getBidId(),
                    bid.getBidAmount(),
                    buyer.getFullName(),
                    bid.getBidTime(),
                    listing.getRemainingQuantity()
            );
        }

        else {
            throw new RuntimeException("Invalid purchase type");
        }}


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
//                .findByListingListingIdOrderByBidAmountDesc(listingId)
//                .stream()
//                .map(bid -> new BidResponseDto(
//                        bid.getBidId(),
//                        bid.getBidAmount(),
//                        bid.getBuyer().getFullName(),
//                        bid.getBidTime()
//                ))
//                .toList();
//
//    }
