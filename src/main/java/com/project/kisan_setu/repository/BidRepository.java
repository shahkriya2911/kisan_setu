package com.project.kisan_setu.repository;
import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.enums.BidStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findByListingListingIdOrderByBuyerAmountDesc(Long listingId);

    Optional<Bid> findTopByListingListingIdOrderByBuyerAmountDesc(Long listingId);

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
}
