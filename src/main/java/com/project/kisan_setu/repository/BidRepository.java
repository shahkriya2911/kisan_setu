package com.project.kisan_setu.repository;

import com.project.kisan_setu.dto.BidResponseDto;
import com.project.kisan_setu.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BidRepository
        extends JpaRepository<Bid, Long> {

    List<Bid> findByListingListingIdOrderByBidAmountDesc(Long listingId);

    Optional<Bid> findTopByListingListingIdOrderByBidAmountDesc(Long listingId);

    List<Bid> findTop5ByListingListingIdOrderByBidAmountDesc(Long listingId);

    @Query("SELECT COUNT(DISTINCT b.buyer.id) FROM Bid b WHERE b.listing.listingId = :listingId")
    long countActiveBidders(@Param("listingId") Long listingId);
}
