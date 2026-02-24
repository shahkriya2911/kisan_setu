package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.LoginResponseDto;
import com.project.kisan_setu.entity.RefreshToken;
import com.project.kisan_setu.entity.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    LoginResponseDto refreshAccessToken(String requestToken);

    void revokeAllUserTokens(User user);

    void revokeToken(String token);
}