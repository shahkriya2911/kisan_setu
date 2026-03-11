package com.project.kisan_setu.repository;
import com.project.kisan_setu.entity.PanCardVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PanCardVerificationRepository extends JpaRepository<PanCardVerification,Long> {
    Optional<PanCardVerification> findByUserUserId(Long userId);

    List<PanCardVerification> findByVerified(boolean b);
}
