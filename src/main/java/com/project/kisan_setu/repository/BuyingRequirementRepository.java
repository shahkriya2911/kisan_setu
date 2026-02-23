package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.BuyingRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository //interface which talks with DB
public interface BuyingRequirementRepository
        extends JpaRepository<BuyingRequirement, Long> {

    List<BuyingRequirement> findByBuyerUserId(Long buyerId); //find a particular buyer (user)
}
