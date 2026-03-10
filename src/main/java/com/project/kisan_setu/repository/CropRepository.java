package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.CropMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CropRepository extends JpaRepository<CropMaster,Long> {
}
