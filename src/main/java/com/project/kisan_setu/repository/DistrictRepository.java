package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.DistrictMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DistrictRepository extends JpaRepository<DistrictMaster,Long> {
    List<DistrictMaster> findByStateStateId(Long stateId);
}
