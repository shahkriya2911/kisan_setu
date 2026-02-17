package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    boolean existsBySellerUserId(Long userId);
}
