package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.entity.RefreshToken;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.repository.RefreshTokenRepository;
import com.project.kisan_setu.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);
    @Value("${jwt.refresh.expiration}")
    private long refreshExpirationMillis;

    @Override
    public RefreshToken createRefreshToken(User user) {
        logger.info("Creating refresh token...");
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setRefreshToken(UUID.randomUUID().toString());
        token.setExpiryDate(
                LocalDateTime.now().plusSeconds(refreshExpirationMillis / 1000)
        ); // for testing
        token.setRevoked(false);
        logger.info("Refresh token creation success...");
        return refreshTokenRepository.save(token);
    }

    @Override
    public RefreshToken validateRefreshToken(String requestToken) {
        logger.info("Validating refresh token...");
        RefreshToken refreshToken = refreshTokenRepository
                .findByRefreshToken(requestToken)
                .orElseThrow(() -> new UserException("Invalid refresh token", HttpStatus.BAD_REQUEST));

        if (refreshToken.isRevoked()) {
            throw new UserException("Refresh token revoked",HttpStatus.UNAUTHORIZED);
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new UserException("Refresh token expired",HttpStatus.UNAUTHORIZED);
        }

        User user = refreshToken.getUser();
        user.getUserId(); // force lazy relation init inside transaction
        logger.info("Refresh token validation success...");
        return refreshToken;
    }

    @Override
    public RefreshToken rotateRefreshToken(String token) {
        logger.info("Rotating refresh token...");
        RefreshToken oldToken = validateRefreshToken(token);
        User user = oldToken.getUser();

        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);
        logger.info("Refresh token rotation success...");
        return createRefreshToken(user);
    }

    // REVOKE ALL
    @Override
    public void revokeAllUserTokens(User user) {
        logger.info("Revoking all user tokens...");
        List<RefreshToken> tokens = refreshTokenRepository.findByUser(user);

        tokens.forEach(token -> token.setRevoked(true));
        logger.info("Revoking all user tokens success...");
        refreshTokenRepository.saveAll(tokens);
    }

    //REVOKE ONE
    @Override
    public void revokeToken(String token) {
        logger.info("Revoking token...");
        RefreshToken refreshToken = refreshTokenRepository
                .findByRefreshToken(token)
                .orElseThrow(() -> new UserException("Token not found",HttpStatus.NOT_FOUND));

        refreshToken.setRevoked(true);
        logger.info("Token revoked success...");
        refreshTokenRepository.save(refreshToken);
    }
}
