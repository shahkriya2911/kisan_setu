package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportUserRepository extends JpaRepository<Report,Long> {
}
