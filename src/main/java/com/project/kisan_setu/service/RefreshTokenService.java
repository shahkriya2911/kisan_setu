package com.project.kisan_setu.service;

import com.project.kisan_setu.entity.RefreshToken;
import com.project.kisan_setu.entity.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken validateRefreshToken(String token);

    RefreshToken rotateRefreshToken(String token);

    void revokeAllUserTokens(User user);

    void revokeToken(String token);
}
