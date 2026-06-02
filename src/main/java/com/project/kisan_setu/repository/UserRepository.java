package com.project.kisan_setu.repository;

import com.project.kisan_setu.dto.RequestDto.UserProfileRequestDto;
import com.project.kisan_setu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndMobileNumber(String email, String mobileNumber);


    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.mobileVerification " +
            "LEFT JOIN FETCH u.aadhaarVerification " +
            "LEFT JOIN FETCH u.bankAccountVerification " +
            "WHERE u.userId = :userId")
    Optional<User> findByIdWithVerifications(@Param("userId") Long userId);


}