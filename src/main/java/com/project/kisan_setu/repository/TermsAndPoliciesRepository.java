package com.project.kisan_setu.repository;

import com.project.kisan_setu.dto.TermsAndPoliciesResponseDto;
import com.project.kisan_setu.entity.TermsAndPolicies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TermsAndPoliciesRepository extends JpaRepository<TermsAndPolicies,Long> {
    Optional<TermsAndPolicies> findTopByOrderByTermsAndPoliciesIdAsc();
}
