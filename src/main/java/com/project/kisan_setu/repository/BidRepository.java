package com.project.kisan_setu.repository;

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

    @Query("SELECT COUNT(DISTINCT b.buyer.userId) FROM Bid b WHERE b.listing.listingId = :listingId")
    Long countActiveBidders(@Param("listingId") Long listingId);

    @Query("SELECT COUNT(b) FROM Bid b WHERE b.listing.seller.userId = :sellerId")
    Long countTotalBidsBySellerId(Long sellerId);

    @Query("""
    SELECT COALESCE(SUM(b.bidAmount), 0)
    FROM Bid b
    WHERE b.listing.seller.userId = :sellerId
    AND b.listing.status = 'SOLD'
    AND b.bidAmount = (
        SELECT MAX(b2.bidAmount)
        FROM Bid b2
        WHERE b2.listing.listingId = b.listing.listingId
    )
""")
    Double sumWinningRevenueBySellerId(@Param("sellerId") Long sellerId);

    @Query("""
       SELECT COALESCE(AVG(b.bidAmount), 0)
       FROM Bid b
       WHERE b.listing.cropName = :cropType
       """)
    Double getAveragePriceByCrop(@Param("cropType") String cropType);


}
