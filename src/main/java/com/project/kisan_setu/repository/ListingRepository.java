package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.enums.ListingStatus;
import com.project.kisan_setu.enums.SaleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing> {
    Long countBySellerUserIdAndSaleTypeAndStatus(
            Long sellerId,
            SaleType saleType,
            ListingStatus status
    );
    List<Listing> findBySaleTypeAndStatus(SaleType saleType, ListingStatus status);
    boolean existsBySellerUserId(Long userId); //checks if that particular user (seller) exists
    List<Listing> findBySaleType(SaleType saleType); //type of listing (fixed or auction)
    Long countBySellerUserIdAndStatus(Long sellerId, ListingStatus status);

    @Query("""
       SELECT COUNT(l)
       FROM Listing l
       WHERE l.saleType = :saleType
       AND l.status = :status
       """)
    Long countLiveAuctions(
            @Param("saleType") SaleType saleType,
            @Param("status") ListingStatus status
    );
}
