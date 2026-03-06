package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.entity.RefreshToken;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.repository.RefreshTokenRepository;
import com.project.kisan_setu.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.refresh.expiration}")
    private long refreshExpirationMillis;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    //CREATE
    @Override
    public RefreshToken createRefreshToken(User user) {

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setRefreshToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusNanos(refreshExpirationMillis * 1_000_000));
        token.setRevoked(false);

        return refreshTokenRepository.save(token);
    }

    @Override
    public RefreshToken validateRefreshToken(String requestToken) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByRefreshToken(requestToken)
                .orElseThrow(() -> new UserException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new UserException("Refresh token revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new UserException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        user.getUserId(); // force lazy relation init inside transaction
        return refreshToken;
    }

    @Override
    public RefreshToken rotateRefreshToken(String token) {
        RefreshToken oldToken = validateRefreshToken(token);
        User user = oldToken.getUser();

        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);

        return createRefreshToken(user);
    }

    // REVOKE ALL
    @Override
    public void revokeAllUserTokens(User user) {

        List<RefreshToken> tokens = refreshTokenRepository.findByUser(user);

        tokens.forEach(token -> token.setRevoked(true));

        refreshTokenRepository.saveAll(tokens);
    }

    //REVOKE ONE
    @Override
    public void revokeToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByRefreshToken(token)
                .orElseThrow(() -> new UserException("Token not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}
