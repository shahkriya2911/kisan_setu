package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.AadhaarVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AadhaarVerificationRepository
        extends JpaRepository<AadhaarVerification, Long> {
    Optional<AadhaarVerification> findByUserUserId(Long userId);

    List<AadhaarVerification> findByVerified(boolean verified);
}