package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.BuyerInquiry;
import com.project.kisan_setu.entity.BuyingRequirement;
import com.project.kisan_setu.enums.InquiryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BuyerInquiryRepository extends JpaRepository<BuyerInquiry,Long> {
    List<BuyerInquiry> findByListingListingId(Long listingId);

    Optional<BuyerInquiry> findByListingListingIdAndStatus(Long listingId, InquiryStatus inquiryStatus);

    List<BuyerInquiry> findByListingSellerUserId(Long userId);

}
