package com.project.kisan_setu.config;

import com.project.kisan_setu.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
public class CookiePrincipalHandshakeHandler extends DefaultHandshakeHandler {

    private static final Logger logger = LoggerFactory.getLogger(CookiePrincipalHandshakeHandler.class);

    private final JwtUtil jwtUtil;

    public CookiePrincipalHandshakeHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return null;
        }

        HttpServletRequest httpRequest = servletRequest.getServletRequest();
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (!"accessToken".equals(cookie.getName())) {
                continue;
            }

            String token = cookie.getValue();
            if (token == null || token.isBlank()) {
                return null;
            }

            try {
                if (!jwtUtil.validateAccessToken(token)) {
                    logger.debug("WebSocket handshake rejected due to invalid access token");
                    return null;
                }

                Long userId = jwtUtil.extractUserId(token);
                logger.debug("WebSocket handshake authenticated for user {}", userId);
                return new StompPrincipal(userId.toString());
            } catch (Exception ex) {
                logger.warn("Failed to authenticate WebSocket handshake from access token", ex);
                return null;
            }
        }

        return null;
    }
}
