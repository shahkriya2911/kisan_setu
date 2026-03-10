package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.PackagingMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackagingRepository extends JpaRepository<PackagingMaster,Long> {
}
