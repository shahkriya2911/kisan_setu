package com.project.kisan_setu.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

        try {

            String accessToken = getCookie(request, "accessToken");

            if (accessToken != null && jwtUtil.validateAccessToken(accessToken)) {

                Long userId = jwtUtil.extractUserId(accessToken);
                setAuth(userId);

            } else {

                handleRefresh(request,response);
            }

        } catch (Exception e) {
            log.error("JWT Filter error", e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private void handleRefresh(HttpServletRequest request,
                               HttpServletResponse response) {

        String refreshToken = getCookie(request, "refreshToken");

        if (refreshToken == null) return;

        try {

            RefreshToken newToken =
                    refreshTokenService.rotateRefreshToken(refreshToken);

            Long userId = newToken.getUser().getUserId();

            String newAccessToken =
                    jwtUtil.generateAccessToken(userId);


            ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .domain("kisansetu.online")
                    .sameSite("None")
                    .maxAge(15 * 60)
                    .build();

            response.addHeader("Set-Cookie", accessCookie.toString());

            setAuth(userId);

        } catch (Exception e) {
            log.warn("Refresh failed");
            SecurityContextHolder.clearContext();
        }
    }

    private String getCookie(HttpServletRequest request, String name) {

        if (request.getCookies() == null) return null;

        for (Cookie c : request.getCookies()) {
            if (name.equals(c.getName())) {
                return c.getValue();
            }
        }

        return null;
    }

    private void setAuth(Long userId) {

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}