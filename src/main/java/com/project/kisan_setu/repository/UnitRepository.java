package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.UnitMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<UnitMaster,Long> {
}
