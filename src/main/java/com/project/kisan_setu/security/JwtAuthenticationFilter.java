package com.project.kisan_setu.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.project.kisan_setu.entity.RefreshToken;
import com.project.kisan_setu.service.RefreshTokenService;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("--- JWT FILTER START ---");
        log.debug("Request URI: {}", request.getRequestURI());

        try {

            String accessToken = getCookieValue(request, "accessToken");

            if (accessToken != null && !accessToken.isBlank()) {

                try {
                    String tokenType = jwtUtil.extractTokenType(accessToken);

                    if ("ACCESS".equals(tokenType)) {
                        Long userId = jwtUtil.extractUserId(accessToken);

                        setAuthentication(userId);

                        log.debug("Access token valid for user: {}", userId);
                    }

                } catch (ExpiredJwtException e) {
                    log.warn("Access token expired → trying refresh");
                    handleRefreshToken(request, response);
                }

            } else {
                log.warn("Access token missing → trying refresh");
                handleRefreshToken(request, response);
            }

        } catch (Exception e) {
            log.error("Unexpected error in JWT filter", e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }


    private void handleRefreshToken(HttpServletRequest request,
                                    HttpServletResponse response) {

        String refreshToken = getCookieValue(request, "refreshToken");

        if (refreshToken != null && !refreshToken.isBlank()) {

            try {

                RefreshToken newToken =
                        refreshTokenService.rotateRefreshToken(refreshToken);

                Long userId = newToken.getUser().getUserId();

                String newAccessToken =
                        jwtUtil.generateAccessToken(userId);


                ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
                        .httpOnly(true)
                        .secure(true)          // required for Vercel/HTTPS
                        .path("/")
                        .sameSite("None")      // required for cross-site cookies
                        .maxAge(jwtUtil.getAccessExpiration() / 1000)
                        .build();


                ResponseCookie refreshCookie = ResponseCookie.from("refreshToken",
                                newToken.getRefreshToken())
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .sameSite("None")
                        .maxAge(jwtUtil.getRefreshExpiration() / 1000)
                        .build();

                response.addHeader("Set-Cookie", accessCookie.toString());
                response.addHeader("Set-Cookie", refreshCookie.toString());

                setAuthentication(userId);

                log.debug("Tokens refreshed successfully for user: {}", userId);

            } catch (Exception ex) {
                log.error("Refresh token failed", ex);
                SecurityContextHolder.clearContext();
            }

        } else {
            log.warn("Refresh token missing");
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Read cookie safely
     */
    private String getCookieValue(HttpServletRequest request, String name) {

        if (request.getCookies() == null) return null;

        for (var cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    /**
     * Set authentication in SecurityContext
     */
    private void setAuthentication(Long userId) {

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}