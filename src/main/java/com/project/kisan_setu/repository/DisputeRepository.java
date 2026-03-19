package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.Dispute;
import com.project.kisan_setu.enums.DisputeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisputeRepository extends JpaRepository<Dispute,Long> {
    List<Dispute> findByStatus(DisputeStatus disputeStatus);
}
