package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository //interface that talks with DB
public interface BidRepository
        extends JpaRepository<Bid, Long> {
    List<Bid> findByListingListingIdOrderByBidAmountDesc(Long listingId); //bid orders of a particular listing
    Optional<Bid> findTopByListingListingIdOrderByBidAmountDesc(Long listingId); //top bids of that particular listing
}
