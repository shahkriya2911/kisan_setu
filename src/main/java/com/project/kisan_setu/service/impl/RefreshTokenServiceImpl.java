package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.LoginResponseDto;
import com.project.kisan_setu.entity.RefreshToken;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.UserMapper;
import com.project.kisan_setu.repository.RefreshTokenRepository;
import com.project.kisan_setu.security.JwtUtil;
import com.project.kisan_setu.service.RefreshTokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
                                   JwtUtil jwtUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    //CREATE
    @Override
    public RefreshToken createRefreshToken(User user) {

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setRefreshToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusDays(7));
        token.setRevoked(false);

        return refreshTokenRepository.save(token);
    }

    //REFRESH
    @Override
    public LoginResponseDto refreshAccessToken(String requestToken) {

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

        // ROTATION
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        RefreshToken newRefreshToken = createRefreshToken(user);
        String newAccessToken = jwtUtil.generateAccessToken(user.getUserId());

        return new LoginResponseDto(
                200,
                "Token refreshed successfully",
                UserMapper.toResponse(user)
        );
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