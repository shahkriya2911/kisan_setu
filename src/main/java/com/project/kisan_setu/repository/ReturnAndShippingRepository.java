package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.ReturnAndShipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnAndShippingRepository extends JpaRepository<ReturnAndShipping,Long> {
    List<ReturnAndShipping> findByUserUserId(Long userId);
}
