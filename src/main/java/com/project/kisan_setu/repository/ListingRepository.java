package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.Listing;
    import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.SaleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository //interface that talks with DB
public interface ListingRepository extends JpaRepository<Listing, Long> {
    boolean existsBySellerUserId(Long userId); //checks if that particular user (seller) exists
    List<Listing> findBySaleType(SaleType saleType); //type of listing (fixed or auction)

    Long countBySellerUserIdAndStatus(Long sellerId, AuctionStatus status);

    List<Listing> findByStatus(AuctionStatus auctionStatus);

    Long countBySaleTypeAndStatus(SaleType saleType, AuctionStatus listingStatus);

    Page<Listing> findAll(Specification<Listing> spec, Pageable pageable);

    List<Listing> findBySellerUserId(Long userId);
}
