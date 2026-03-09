package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.RefreshToken;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.UserMapper;
import com.project.kisan_setu.repository.RefreshTokenRepository;
import com.project.kisan_setu.security.JwtUtil;
import com.project.kisan_setu.service.RefreshTokenService;
import com.project.kisan_setu.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController //handling of rest apis
@RequestMapping("api/users")
@RequiredArgsConstructor//api starts with /users
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    @Value("${auth.cookie.secure:false}")
    private boolean secureCookie;
    private final RefreshTokenRepository refreshTokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);



    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(
            @Valid @RequestBody CreateUserRequestDto dto,
            HttpServletResponse response) {

        logger.debug("Signup request for email: {}", dto.getEmail());

        SignupResponseDto responseDto = userService.signup(dto);
        User user = userService.findByEmail(responseDto.getData().getEmail());
        issueLoginCookies(response, user);

        logger.info("Signup successful for email: {}", user.getEmail());
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto dto,
            HttpServletResponse response) {

        logger.debug("Login request for email: {}", dto.getEmail());

        LoginResponseDto responseDto = userService.login(dto);
        User user = userService.findByEmail(responseDto.getData().getEmail());
        issueLoginCookies(response, user);

        logger.info("Login successful for email: {}", user.getEmail());
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse,
            @RequestBody(required = false) RefreshTokenRequestDto request) {
        String token = resolveRefreshToken(servletRequest, request);
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new LoginResponseDto(HttpStatus.UNAUTHORIZED.value(), "Refresh token missing", null)
            );
        }

        try {
            RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(token);
            User user = newRefreshToken.getUser();
            writeTokenCookies(
                    servletResponse,
                    jwtUtil.generateAccessToken(user.getUserId()),
                    newRefreshToken.getRefreshToken()
            );
            logger.info("Token refresh successfully for user with id : {}",user.getUserId());
            return ResponseEntity.ok(
                    new LoginResponseDto(
                            HttpStatus.OK.value(),
                            "Token refreshed successfully",
                            UserMapper.toResponse(user)
                    )
            );
        } catch (UserException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new LoginResponseDto(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), null)
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse,
            @RequestBody(required = false) RefreshTokenRequestDto request) {
        logger.debug("logout request attempt for user");
        String token = resolveRefreshToken(servletRequest, request);
        if (token != null && !token.isBlank()) {
            try {
                refreshTokenService.revokeToken(token);
            } catch (UserException ignored) {
                // logout remains idempotent
            }
        }
        clearAuthCookies(servletResponse);
        logger.info("User logged out successfully");
        return ResponseEntity.ok("Logged out user successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<UserResponseDto>>> getAllUsers() {
        logger.info("Get all users request");
        List<UserResponseDto> users = userService.getAllUsers()
                .stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());

        ApiResponseDto<List<UserResponseDto>> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "Users fetched successfully",
                users
        );

        logger.info("All users fetched successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getUserById(@PathVariable Long userId) {
        logger.debug("Get user by id request for user with id: {}", userId);
        User user = userService.getUserById(userId);

        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "User fetched successfully",
                new UserResponseDto(user)
        );

        logger.info("User with id: {} fetched successfully", userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getSession(Authentication authentication) {
        logger.debug("Get session for user with id : {}",Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        User user = userService.getUserById(userId);
        logger.info("Session active successfully");
        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        HttpStatus.OK.value(),
                        "Session active",
                        new UserResponseDto(user)
                )
        );
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequestDto dto) {
        logger.debug("Update user by id request for user with id: {}", userId);

        UserResponseDto updatedUser = userService.updateUserById(userId, dto);

        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "User updated successfully",
                updatedUser
        );

        logger.info("User with id {} updated successfully",userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteUser(@PathVariable Long userId) {
        logger.debug("Delete user request for user with id: {}", userId);
        userService.deleteUserById(userId);

        ApiResponseDto<Void> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "User deleted successfully",
                null
        );
        logger.info("User with id {} deleted successfully",userId);
        return ResponseEntity.ok(response);
    }

    private void issueLoginCookies(HttpServletResponse response, User user) {
        logger.debug("Issuing login cookies for user... with id : {}",user.getUserId());
        refreshTokenService.revokeAllUserTokens(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        logger.info("Login cookies issued successfully");
        writeTokenCookies(
                response,
                jwtUtil.generateAccessToken(user.getUserId()),
                refreshToken.getRefreshToken()
        );
    }

    private void writeTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(jwtUtil.getAccessExpiration() / 1000)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(jwtUtil.getRefreshExpiration() / 1000)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    private void clearAuthCookies(HttpServletResponse response) {
        ResponseCookie clearAccessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        ResponseCookie clearRefreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", clearAccessCookie.toString());
        response.addHeader("Set-Cookie", clearRefreshCookie.toString());
    }

    private String resolveRefreshToken(HttpServletRequest request, RefreshTokenRequestDto requestBody) {
        if (requestBody != null
                && requestBody.getRefreshToken() != null
                && !requestBody.getRefreshToken().isBlank()) {
            return requestBody.getRefreshToken();
        }
        return readCookieValue(request, "refreshToken").orElse(null);
    }

    private Optional<String> readCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return Optional.ofNullable(cookie.getValue());
            }
        }
        return Optional.empty();
    }
}
