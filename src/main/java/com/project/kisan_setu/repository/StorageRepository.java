package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.StorageMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<StorageMaster,Long> {
}
