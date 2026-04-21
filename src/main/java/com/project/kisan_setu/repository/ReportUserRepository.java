package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.Report;
import com.project.kisan_setu.enums.ReportedBy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportUserRepository extends JpaRepository<Report,Long> {
    boolean existsByOrder_OrderIdAndReportedBy(Long orderId, ReportedBy reportedBy);

    boolean existsByOrder_OrderIdAndBuyer_UserIdAndIsBuyerReportedTrue(Long orderId, Long buyerId);

    boolean existsByOrder_OrderIdAndSeller_UserIdAndIsSellerReportedTrue(Long orderId, Long sellerId);
}
