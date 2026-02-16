package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.SellerAddCrop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerAddCropRepository extends JpaRepository<SellerAddCrop, Long> {
}
