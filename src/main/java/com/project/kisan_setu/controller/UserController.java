package com.project.kisan_setu.controller;


import com.project.kisan_setu.dto.RequestDto.AccountSettingRequestDto;
import com.project.kisan_setu.dto.RequestDto.ChangePasswordRequestDto;
import com.project.kisan_setu.dto.RequestDto.CreateUserRequestDto;
import com.project.kisan_setu.dto.RequestDto.LoginRequestDto;
import com.project.kisan_setu.dto.RequestDto.RefreshTokenRequestDto;
import com.project.kisan_setu.dto.RequestDto.UpdateUserRequestDto;
import com.project.kisan_setu.dto.RequestDto.UserProfileRequestDto;
import com.project.kisan_setu.dto.ResponseDto.AccountSettingResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ApiResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ChangePasswordResponseDto;
import com.project.kisan_setu.dto.ResponseDto.KycStatusResponseDto;
import com.project.kisan_setu.dto.ResponseDto.LoginResponseDto;
import com.project.kisan_setu.dto.ResponseDto.SignupResponseDto;
import com.project.kisan_setu.dto.ResponseDto.UserProfileResponseDto;
import com.project.kisan_setu.dto.ResponseDto.UserResponseDto;
import com.project.kisan_setu.entity.RefreshToken;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.UserMapper;
import com.project.kisan_setu.repository.RefreshTokenRepository;
import com.project.kisan_setu.security.JwtUtil;
import com.project.kisan_setu.service.RefreshTokenService;
import com.project.kisan_setu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for users related resources")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    @Value("${auth.cookie.secure:false}")
    private boolean secureCookie;
    private final RefreshTokenRepository refreshTokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/signup")
    @Operation(description = "This method is for user registration", summary = "Sign up method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Incorrect credentials"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    public ResponseEntity<SignupResponseDto> signup(
            @Parameter(description = "user registration details are required", required = true) @Valid @RequestBody CreateUserRequestDto dto,
            HttpServletResponse response) {

        logger.debug("Signup request for email: {}", dto.getEmail());

        SignupResponseDto responseDto = userService.signup(dto);
        User user = userService.findByEmail(responseDto.getData().getEmail());
        issueLoginCookies(response, user);

        logger.info("Signup successful for email: {}", user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Login method", description = "This method is used for user login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "401", description = "Bad user credentials"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    public ResponseEntity<LoginResponseDto> login(
            @Parameter(description = "user login credentials", required = true) @Valid @RequestBody LoginRequestDto dto,
            HttpServletResponse response) {

        logger.debug("Login request for email: {}", dto.getEmail());

        LoginResponseDto responseDto = userService.login(dto);
        User user = userService.findByEmail(responseDto.getData().getEmail());
        issueLoginCookies(response, user);

        logger.info("Login successful for email: {}", user.getEmail());
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token method", description = "This method is used to refresh access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Refresh token missing or invalid")
    })
    public ResponseEntity<LoginResponseDto> refreshToken(
            @Parameter(description = "HTTP servlet request") HttpServletRequest servletRequest,
            @Parameter(description = "HTTP servlet response") HttpServletResponse servletResponse,
            @Parameter(description = "Refresh token request body") @RequestBody(required = false) RefreshTokenRequestDto request) {
        String token = resolveRefreshToken(servletRequest, request);
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new LoginResponseDto(HttpStatus.UNAUTHORIZED.value(), "Refresh token missing", null));
        }

        try {
            RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(token);
            User user = newRefreshToken.getUser();
            writeTokenCookies(
                    servletResponse,
                    jwtUtil.generateAccessToken(user.getUserId()),
                    newRefreshToken.getRefreshToken());
            logger.info("Token refresh successfully for user with id : {}", user.getUserId());
            return ResponseEntity.ok(
                    new LoginResponseDto(
                            HttpStatus.OK.value(),
                            "Token refreshed successfully",
                            UserMapper.toResponse(user)));
        } catch (UserException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new LoginResponseDto(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), null));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout method", description = "This method is used to logout user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged out successfully"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    public ResponseEntity<String> logout(
            @Parameter(description = "HTTP servlet request") HttpServletRequest servletRequest,
            @Parameter(description = "HTTP servlet response") HttpServletResponse servletResponse,
            @Parameter(description = "Refresh token request body") @RequestBody(required = false) RefreshTokenRequestDto request) {
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
    @Operation(summary = "Get all users method", description = "This method is used to get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<ApiResponseDto<List<UserResponseDto>>> getAllUsers() {
        logger.info("Get all users request");
        List<UserResponseDto> users = userService.getAllUsers()
                .stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());

        ApiResponseDto<List<UserResponseDto>> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "Users fetched successfully",
                users);

        logger.info("All users fetched successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID method", description = "This method is used to get user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getUserById(
            @Parameter(description = "User ID path variable", required = true) @PathVariable Long userId) {
        logger.debug("Get user by id request for user with id: {}", userId);
        User user = userService.getUserById(userId);

        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "User fetched successfully",
                new UserResponseDto(user));

        logger.info("User with id: {} fetched successfully", userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session")
    @Operation(summary = "Get session method", description = "This method is used to get current user session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session active"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getSession(
            @Parameter(description = "Authentication object") Authentication authentication) {
        logger.debug("Get session for user with id : {}", Long.parseLong(authentication.getName()));
        Long userId = Long.parseLong(authentication.getName());
        User user = userService.getUserById(userId);
        logger.info("Session active successfully");
        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        HttpStatus.OK.value(),
                        "Session active",
                        new UserResponseDto(user)));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user method", description = "This method is used to update user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Bad input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> updateUser(
            @Parameter(description = "User ID path variable", required = true) @PathVariable Long userId,
            @Parameter(description = "Update user request body", required = true) @Valid @RequestBody UpdateUserRequestDto dto) {
        logger.debug("Update user by id request for user with id: {}", userId);

        UserResponseDto updatedUser = userService.updateUserById(userId, dto);

        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "User updated successfully",
                updatedUser);

        logger.info("User with id {} updated successfully", userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user method", description = "This method is used to delete user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<ApiResponseDto<Void>> deleteUser(
            @Parameter(description = "User ID path variable", required = true) @PathVariable Long userId) {
        logger.debug("Delete user request for user with id: {}", userId);
        userService.deleteUserById(userId);

        ApiResponseDto<Void> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "User deleted successfully",
                null);
        logger.info("User with id {} deleted successfully", userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/completeProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Complete user profile method", description = "This method is used to complete user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile completed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<UserProfileResponseDto> completeUserProfile(
            @Parameter(description = "User profile request data", required = true) @ModelAttribute UserProfileRequestDto dto) {
        UserProfileResponseDto response = userService.completeUserProfileData(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/profile-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload profile photo method", description = "This method is used to upload user profile photo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile photo uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Bad input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<UserProfileResponseDto> uploadProfilePhoto(
            @Parameter(description = "Profile photo file", required = true) @RequestParam("file") MultipartFile file) {

        UserProfileResponseDto response = userService.uploadProfilePhoto(file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account-settings")
    @Operation(summary = "Get account settings method", description = "This method is used to get user account settings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account settings fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<AccountSettingResponseDto> getAccountSettings() {
        AccountSettingResponseDto response = userService.getAccountSettings();
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/account-settings", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update account settings method", description = "This method is used to update user account settings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account settings updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<AccountSettingResponseDto> updateAccountSettings(
            @Parameter(description = "Account settings request data", required = true) @ModelAttribute AccountSettingRequestDto dto) {
        AccountSettingResponseDto response = userService.updateAccountSettings(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password method", description = "This method is used to change user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<ChangePasswordResponseDto> changePassword(
            @Parameter(description = "Change password request data", required = true) @Valid @RequestBody ChangePasswordRequestDto dto) {

        ChangePasswordResponseDto response = userService.changePassword(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/kyc-status")
    @Operation(summary = "Get KYC status method", description = "This method is used to get user KYC status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "KYC status fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<KycStatusResponseDto> getKycStatus() {
        logger.debug("KYC status request attempt");
        return ResponseEntity.ok(userService.getKycStatus());
    }

    private void issueLoginCookies(HttpServletResponse response, User user) {
        logger.debug("Issuing login cookies for user... with id : {}", user.getUserId());
        refreshTokenService.revokeAllUserTokens(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        logger.info("Login cookies issued successfully");
        writeTokenCookies(
                response,
                jwtUtil.generateAccessToken(user.getUserId()),
                refreshToken.getRefreshToken());
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