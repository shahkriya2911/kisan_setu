package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email); //checks if email exists in DB
    boolean existsByMobileNumber(String mobileNumber); //checks if mobile number exists in DB
    Optional<User> findByEmail(String email); //find email
}
