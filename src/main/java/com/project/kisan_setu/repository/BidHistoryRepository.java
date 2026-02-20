package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.BidHistory;
import com.project.kisan_setu.entity.Listing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidHistoryRepository extends JpaRepository<BidHistory, Long> {


    long countByListing_ListingId(Long listingId);
}
