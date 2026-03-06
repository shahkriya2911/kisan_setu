package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.MobileVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MobileVerificationRepository extends JpaRepository<MobileVerification, Long> {

    Optional<MobileVerification> findByUserUserId(Long userId);
}