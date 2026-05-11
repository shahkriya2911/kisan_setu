package com.project.kisan_setu.repository;

import com.project.kisan_setu.dto.ResponseDto.TopCommodityDto;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.SaleType;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository // interface that talks with DB
public interface ListingRepository extends JpaRepository<Listing, Long> {
        boolean existsBySellerUserId(Long userId); // checks if that particular user (seller) exists

        List<Listing> findBySaleType(SaleType saleType); // type of listing (fixed or auction)

        Page<Listing> findBySeller_UserIdAndStatus(Long sellerId, AuctionStatus status, Pageable pageable);

        Page<Listing> findBySeller_UserIdAndStatusAndCrop_CropNameContainingIgnoreCase(
                        Long sellerId, AuctionStatus status, String cropName, Pageable pageable);

        Long countBySeller_UserIdAndStatus(Long sellerId, AuctionStatus status);

        List<Listing> findBySaleTypeAndSellerUserIdNotAndStatusAndAuctionEndTimeAfter(SaleType saleType, Long sellerId,
                        AuctionStatus auctionStatus, LocalDateTime time);

        Page<Listing> findBySaleTypeAndSellerUserIdNotAndStatus(SaleType saleType, Long sellerId,
                        AuctionStatus auctionStatus, Pageable pageable);

        List<Listing> findByStatus(AuctionStatus auctionStatus);

        Long countBySaleTypeAndStatus(SaleType saleType, AuctionStatus listingStatus);

        Page<Listing> findAll(Specification<Listing> spec, Pageable pageable);

        List<Listing> findBySellerUserId(Long userId);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT l FROM Listing l WHERE l.listingId = :listingId")
        Optional<Listing> findByIdForUpdate(@Param("listingId") Long listingId);

        Page<Listing> findBySeller_UserId(Long userId, Pageable pageable);

        @Query("""
                        SELECT l FROM Listing l
                        WHERE l.seller.userId = :sellerId
                        AND (:status IS NULL OR l.status = :status)
                        AND (
                            :search IS NULL
                            OR LOWER(l.crop.cropName) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR LOWER(l.variety) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR LOWER(l.grade) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR LOWER(l.state.name) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR LOWER(l.district.name) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR LOWER(l.unit.unitName) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR LOWER(CAST(l.saleType AS string)) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR LOWER(CAST(l.purchaseType AS string)) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR LOWER(CAST(l.status AS string)) LIKE LOWER(CONCAT('%', :search, '%'))
                        )
                        """)
        Page<Listing> searchSellerListings(
                        @Param("sellerId") Long sellerId,
                        @Param("status") AuctionStatus status,
                        @Param("search") String search,
                        Pageable pageable);

        Page<Listing> findBySeller_UserIdAndStatusAndSaleType(Long sellerId, AuctionStatus auctionStatus,
                        SaleType saleType,
                        Pageable pageable);

        @Query("SELECT COUNT(DISTINCT l.seller) FROM Listing l")
        long countDistinctSellers();

        long countByStatus(AuctionStatus status);

        @Query("SELECT l.crop.cropName, COUNT(l) FROM Listing l GROUP BY l.crop.cropName ORDER BY COUNT(l) DESC")
        List<Object[]> getTopCommodities();

        long countBySellerUserId(Long userId);

        // ACTIVE AUCTION LISTINGS
        @Query("""
    SELECT l FROM Listing l
    WHERE l.saleType = :saleType
    AND l.status = :status
    AND l.seller.userId <> :userId
    AND (:cropNamePattern IS NULL OR LOWER(l.crop.cropName) LIKE :cropNamePattern)
""")
        List<Listing> findActiveAuctionListings(
                        @Param("saleType") SaleType saleType,
                        @Param("status") AuctionStatus status,
                        @Param("userId") Long userId,
                        @Param("cropNamePattern") String cropNamePattern);

        // ACTIVE FIXED LISTINGS
        @Query("""
                            SELECT l FROM Listing l
                            WHERE l.saleType = :saleType
                            AND l.status = :status
                            AND l.seller.userId <> :userId
                            AND (:cropNamePattern IS NULL OR LOWER(l.crop.cropName) LIKE :cropNamePattern)
                        """)
        List<Listing> findActiveFixedListings(
                        @Param("saleType") SaleType saleType,
                        @Param("status") AuctionStatus status,
                        @Param("userId") Long userId,
                        @Param("cropNamePattern") String cropNamePattern);

        // CLOSE EXPIRED AUCTIONS (BULK UPDATE)
        @Modifying
        @Query("""
                            UPDATE Listing l
                            SET l.status = 'CLOSED'
                            WHERE l.saleType = 'AUCTION'
                            AND l.status = 'ACTIVE'
                            AND l.auctionEndTime < CURRENT_TIMESTAMP
                        """)
        int closeExpiredAuctions();

        @Modifying
        @Transactional
        @Query("""
                            UPDATE Listing l
                            SET l.status = 'CLOSED'
                            WHERE l.saleType = 'FIXED'
                            AND l.status = 'ACTIVE'
                            AND l.auctionEndTime < CURRENT_TIMESTAMP
                        """)
        int closeExpiredFixedListings();
}
