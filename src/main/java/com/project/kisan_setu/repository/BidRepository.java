package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BidRepository
        extends JpaRepository<Bid, Long> {

    List<Bid> findByListingListingIdOrderByBidAmountDesc(Long listingId);

    Optional<Bid> findTopByListingListingIdOrderByBidAmountDesc(Long listingId);
}
