package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.StateMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<StateMaster, Long> {
}