package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.RefreshToken;
import com.project.kisan_setu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Find token by token string
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    // Get all tokens of a user
    List<RefreshToken> findByUser(User user);

    // Delete all tokens of a user
    void deleteByUser(User user);
}