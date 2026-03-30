package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.BuyingRequirement;
import com.project.kisan_setu.enums.RequirementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyingRequirementRepository
        extends JpaRepository<BuyingRequirement, Long> {

    List<BuyingRequirement> findByBuyerUserId(Long buyerId); //find a particular buyer (user)

    List<BuyingRequirement> findByCrop_CropNameIgnoreCase(String cropName);

    List<BuyingRequirement> findByRequirementStatusAndBuyer_UserIdNot(RequirementStatus requirementStatus, Long currentUserId);
}
