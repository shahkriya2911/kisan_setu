package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.ChangingLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChangingLanguageRepository extends JpaRepository<ChangingLanguage,Long> {
    Optional<ChangingLanguage> findByUserUserId(Long userId);
}
