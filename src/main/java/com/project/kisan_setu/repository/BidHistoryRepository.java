package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.BidHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BidHistoryRepository extends JpaRepository<BidHistory, Long> {
    @Query("SELECT COUNT(l) FROM Listing l WHERE l.saleType='AUCTION' AND l.status='ACTIVE'")
    Long countLiveAuctions();
    @Query("SELECT AVG(l.pricePerKg) FROM Listing l WHERE l.cropName = :crop")
    Double getAveragePriceByCrop(String crop);
    long countByListing_ListingId(Long listingId);
}
