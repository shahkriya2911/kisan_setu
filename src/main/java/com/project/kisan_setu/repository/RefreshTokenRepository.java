package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.RefreshToken;
import com.project.kisan_setu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {


    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    List<RefreshToken> findByUser(User user);

    void deleteByUser(User user);
}