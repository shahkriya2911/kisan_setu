package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.MarketFilterRequestDto;
import com.project.kisan_setu.dto.MarketInsightDto;
import com.project.kisan_setu.dto.MarketListingResponseDto;
import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.SaleType;
import com.project.kisan_setu.mapper.MarketMapper;
import com.project.kisan_setu.repository.BidRepository;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.service.MarketService;
import com.project.kisan_setu.specification.ListingSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MarketServiceImpl implements MarketService {

    private final ListingRepository listingRepository;
    private final BidRepository bidRepository;
    private final MarketMapper marketMapper;

    @Override
    public Page<MarketListingResponseDto> getLiveListings(
            MarketFilterRequestDto filter,
            Pageable pageable) {

        Specification<Listing> spec = ListingSpecification.filterListings(filter);

        Page<Listing> listings = listingRepository.findAll(spec, pageable);

        return listings.map(listing -> {

            BigDecimal highestBid = null;

            if (listing.getSaleType() == SaleType.AUCTION) {
                highestBid = bidRepository
                        .findTopByListingListingIdOrderByBuyerAmountDesc(listing.getListingId())
                        .map(Bid::getBuyerAmount)
                        .orElse(listing.getTotalBasePrice());
            }

            return marketMapper.toDto(listing, highestBid);
        });
    }

    @Override
    public MarketInsightDto getMarketInsights() {

        Double avgWheat = bidRepository.getAveragePriceByCrop("Wheat");
        Long liveAuctions = listingRepository.countBySaleTypeAndStatus(
                SaleType.AUCTION,
                AuctionStatus.ACTIVE
        );
        return new MarketInsightDto(
                avgWheat,
                liveAuctions,
                "Basmati Rice"
        );
    }
}
