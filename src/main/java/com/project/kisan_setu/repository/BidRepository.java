package com.project.kisan_setu.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.BidStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByBuyerUserIdAndBidStatusInOrderByCreatedAtAsc(Long buyerId, List<BidStatus> statuses);

    List<Bid> findByBuyerUserIdAndBidStatusOrderByCreatedAtAsc(Long buyerId, BidStatus bidStatus);

    List<Bid> findByListingListingIdOrderByBuyerAmountDesc(Long listingId);

    Optional<Bid> findTopByListingListingIdAndBidStatusOrderByBuyerAmountDesc(
            Long listingId, BidStatus status);

    default List<Bid> findTop5ByListingListingIdAndBidStatusOrderByAmountDesc(Long listingId, BidStatus status) {
        return findTop5ByListingListingIdAndBidStatusOrderByBuyerAmountDesc(listingId, status);
    }

    List<Bid> findTop5ByListingListingIdAndBidStatusOrderByBuyerAmountDesc(Long listingId, BidStatus status);

    @Query("""
            SELECT b FROM Bid b
            JOIN b.listing l
            WHERE b.buyer.userId = :buyerId
            AND b.bidStatus = :status
            AND l.status = :Status
            AND b.buyerAmount < (
                SELECT MAX(b2.buyerAmount)
                FROM Bid b2
                WHERE b2.listing.listingId = b.listing.listingId
            )
            """)
    List<Bid> findOutbidBids(Long buyerId, BidStatus status, AuctionStatus Status);

    Optional<Bid> findTopByListingListingIdOrderByBuyerAmountDesc(Long listingId);

    Optional<Bid> findTopByListingListingIdOrderByBidIdDesc(Long listingId);

    List<Bid> findTop5ByListingListingIdOrderByBuyerAmountDesc(Long listingId);

    @Query("SELECT COUNT(DISTINCT b.buyer.userId) FROM Bid b WHERE b.listing.listingId = :listingId")
    Long countActiveBidders(@Param("listingId") Long listingId);

    @Query("SELECT COUNT(b) FROM Bid b WHERE b.listing.seller.userId = :sellerId")
    Long countTotalBidsBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT COALESCE(SUM(b.buyerAmount), 0) FROM Bid b WHERE b.listing.seller.userId = :sellerId")
    BigDecimal sumAmountByListingSellerId(@Param("sellerId") Long sellerId);

    List<Bid> findByListing(Listing listing);

    @Query("SELECT AVG(l.pricePerKg) FROM Listing l WHERE l.crop.cropName = :crop")
    Double getAveragePriceByCrop(@Param("crop") String crop);

    List<Bid> findTop5ByListingSellerUserIdAndBidStatusOrderByCreatedAtDesc(Long userId, BidStatus bidStatus);

    @Query("SELECT COUNT(l) FROM Listing l WHERE l.saleType='AUCTION' AND l.status='ACTIVE'")
    Long countLiveAuctions();

    long countByListing_ListingId(Long listingId);

    @Query("SELECT MAX(b.buyerAmount) FROM Bid b WHERE b.listing.id = :listingId")
    BigDecimal getHighestBid(@Param("listingId") Long listingId);

    boolean existsByListingAndBidStatus(Listing listing, BidStatus bidStatus);

    List<Bid> findByBidStatusAndAcceptedTimeBefore(BidStatus bidStatus, LocalDateTime cutoff);

    List<Bid> findByListingListingIdAndBidIdNot(Long listingId, Long bidId);
}
