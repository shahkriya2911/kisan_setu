package com.project.kisan_setu.security;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

            log.debug("Access Token: {}", accessToken);

            if (accessToken != null && !accessToken.isBlank()) {

                try {
                    log.debug("Validating access token...");

                    String tokenType = jwtUtil.extractTokenType(accessToken);

                    if ("ACCESS".equals(tokenType)) {
                        Long userId = jwtUtil.extractUserId(accessToken);

                        setAuthentication(userId);

                        log.debug(" Access token VALID. User: {}", userId);
                    }

                } catch (ExpiredJwtException e) {
                    log.warn(" Access token expired → trying refresh");
                    handleRefreshToken(request, response);
                }

            } else {

                log.warn(" Access token missing → trying refresh");
                handleRefreshToken(request, response);
            }

        } catch (Exception e) {
            log.error(" Unexpected error in JWT filter", e);
            SecurityContextHolder.clearContext();
        }


        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            log.debug("Final Authentication: {}",
                    SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        } else {
            log.warn(" No authentication - 403 likely");
        }

        log.debug("-- JWT FILTER END ---");
        filterChain.doFilter(request, response);
    }

    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = getCookieValue(request, "refreshToken");

        log.debug("Refresh Token: {}", refreshToken);

        if (refreshToken != null && !refreshToken.isBlank()) {
            try {

                log.debug("Rotating refresh token...");


                RefreshToken newToken = refreshTokenService.rotateRefreshToken(refreshToken);

                Long userId = newToken.getUser().getUserId();

                log.debug("Refresh token valid for user: {}", userId);


                String newAccessToken = jwtUtil.generateAccessToken(userId);


                Cookie accessCookie = new Cookie("accessToken", newAccessToken);
                accessCookie.setHttpOnly(true);
                accessCookie.setPath("/");
                accessCookie.setMaxAge((int) (jwtUtil.getAccessExpiration() / 1000));

                response.addCookie(accessCookie);

                log.debug(" New access token cookie set");


                Cookie refreshCookie = new Cookie("refreshToken", newToken.getRefreshToken());
                refreshCookie.setHttpOnly(true);
                refreshCookie.setPath("/");
                refreshCookie.setMaxAge((int) (jwtUtil.getRefreshExpiration() / 1000));

                response.addCookie(refreshCookie);

                log.debug(" New refresh token cookie set");

                setAuthentication(userId);

                log.debug(" User authenticated after refresh: {}", userId);

            } catch (Exception ex) {
                log.error(" Refresh failed - invalid/expired token", ex);
                SecurityContextHolder.clearContext();
            }

        } else {
            log.warn(" Refresh token NOT found");
            SecurityContextHolder.clearContext();
        }
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void setAuthentication(Long userId) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}