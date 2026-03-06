package com.project.kisan_setu.repository;
import com.project.kisan_setu.entity.BankAccountVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface BankAccountVerificationRepository extends JpaRepository<BankAccountVerification, Long> {

    Optional<BankAccountVerification> findByUserUserId(Long userId);

    List<BankAccountVerification> findByVerified(boolean b);
}