package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.enums.SaleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    boolean existsBySellerUserId(Long userId);

    List<Listing> findBySaleType(SaleType saleType);
}
