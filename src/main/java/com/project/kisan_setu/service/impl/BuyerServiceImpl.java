package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.BuyingRequirement;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.SaleType;
import com.project.kisan_setu.mapper.BuyingRequirementMapper;
import com.project.kisan_setu.repository.BidRepository;
import com.project.kisan_setu.repository.BuyingRequirementRepository;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.repository.UserRepository;
import com.project.kisan_setu.service.BuyerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BuyerServiceImpl implements BuyerService {

    private final BuyingRequirementRepository requirementRepository;
    private final ListingRepository listingRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;

    public BuyerServiceImpl(
            BuyingRequirementRepository requirementRepository,
            ListingRepository listingRepository,
            BidRepository bidRepository,
            UserRepository userRepository) {
        this.requirementRepository = requirementRepository;
        this.listingRepository = listingRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
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
    public BidResponseDto placeBid(
            Long buyerId,
            Long listingId,
            PlaceBidRequestDto dto) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!listing.getSaleType().equals(SaleType.AUCTION)) {
            throw new RuntimeException("Not an auction listing");
        }

        Double currentHighest = bidRepository
                .findTopByListingListingIdOrderByBidAmountDesc(listingId)
                .map(Bid::getBidAmount)
                .orElse(listing.getPricePerKg());

        if (dto.getBidAmount() <= currentHighest) {
            throw new RuntimeException("Bid must be higher than current bid");
        }

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

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
                bid.getBidTime()
        );
    }

    // Bid History
    @Override
    public List<BidResponseDto> getBidHistory(Long listingId) {

        return bidRepository
                .findByListingListingIdOrderByBidAmountDesc(listingId)
                .stream()
                .map(bid -> new BidResponseDto(
                        bid.getBidId(),
                        bid.getBidAmount(),
                        bid.getBuyer().getFullName(),
                        bid.getBidTime()
                ))
                .toList();
    }
}