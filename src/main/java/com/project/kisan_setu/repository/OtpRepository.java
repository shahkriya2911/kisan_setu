package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.OrderOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OrderOtp, Long> {


    Optional<OrderOtp> findTopByOrderIdAndBuyerIdOrderByIdDesc(Long orderId, Long buyerId);

    Optional<OrderOtp> findByOrderIdAndBuyerIdAndVerifiedTrue(Long orderId, Long buyerId);
}
