package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchemeRepository extends JpaRepository<Scheme,Long> {
}
