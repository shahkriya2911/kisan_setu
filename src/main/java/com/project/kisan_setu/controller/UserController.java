package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.RequestDto.ChangePasswordRequestDto;
import com.project.kisan_setu.dto.RequestDto.CreateUserRequestDto;
import com.project.kisan_setu.dto.RequestDto.LoginRequestDto;
import com.project.kisan_setu.dto.RequestDto.RefreshTokenRequestDto;
import com.project.kisan_setu.dto.RequestDto.UpdateUserRequestDto;
import com.project.kisan_setu.dto.RequestDto.UserProfileRequestDto;
import com.project.kisan_setu.dto.ResponseDto.*;
import com.project.kisan_setu.entity.RefreshToken;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.UserMapper;
import com.project.kisan_setu.repository.RefreshTokenRepository;
import com.project.kisan_setu.repository.UserRepository;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.project.kisan_setu.dto.RequestDto.ForgotPasswordRequestDto;
import java.util.List;
import java.util.Optional;

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
        private final UserRepository userRepository;
        private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/signup")
    @Operation(
            description = "This method is for user registration",
            summary = "Sign up method"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Incorrect credentials"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    public ResponseEntity<SignupResponseDto> signup(

            @Parameter(
                    description = "user registration details are required",
                    required = true
            )
            @Valid
            @RequestBody
            CreateUserRequestDto dto,

            HttpServletResponse response
    ) {

        long startTime = System.currentTimeMillis();

        logger.debug("Signup request for email: {}", dto.getEmail());

        User user = userService.signup(dto);

        issueLoginCookies(response, user);

        SignupResponseDto responseDto =
                new SignupResponseDto(
                        201,
                        "Registration successful",
                        UserMapper.toResponse(user)
                );

        long endTime = System.currentTimeMillis();

        logger.info(
                "Signup successful for email: {} | Time Taken: {} ms",
                user.getEmail(),
                (endTime - startTime)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login method",
            description = "This method is used for user login"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "400", description = "Bad user credentials"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    public ResponseEntity<LoginResponseDto> login(

            @Parameter(
                    description = "user login credentials",
                    required = true
            )
            @Valid
            @RequestBody
            LoginRequestDto dto,

            HttpServletResponse response
    ) {

        long startTime = System.currentTimeMillis();

        logger.debug("Login request for email: {}", dto.getEmail());

        User user = userService.login(dto);

        issueLoginCookies(response, user);

        LoginResponseDto responseDto =
                new LoginResponseDto(
                        200,
                        "Login successful",
                        UserMapper.toResponse(user)
                );

        long endTime = System.currentTimeMillis();

        logger.info(
                "Login successful for email: {} | Time Taken: {} ms",
                user.getEmail(),
                (endTime - startTime)
        );

        return ResponseEntity.ok(responseDto);
    }
        @GetMapping("/me")
        @Operation(summary = "user details method", description = "This method is used for user details")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "User details fetched successfully"),
                @ApiResponse(responseCode = "400", description = "Bad user credentials"),
                @ApiResponse(responseCode = "500", description = "Something went wrong")
        })
        @SecurityRequirement(name = "cookieAuth")
        public ResponseEntity<CurrentUserDto> userDetails(){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.parseLong(authentication.getName());
            User user = userRepository.findById(userId).
                    orElseThrow(()->new RuntimeException("User not found"));
            CurrentUserDto response = new CurrentUserDto(user.getUserId(),user.getFullName(),user.getEmail());
            return ResponseEntity.ok(response);
        }

        @PostMapping("/refresh")
        @Operation(summary = "Refresh token method", description = "Refresh access token using refresh token from cookie")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
                        @ApiResponse(responseCode = "401", description = "Refresh token missing or invalid")
        })
        public ResponseEntity<LoginResponseDto> refreshToken(
                        HttpServletRequest request,
                        HttpServletResponse response) {

                String refreshToken = null;

                if (request.getCookies() != null) {
                        for (Cookie cookie : request.getCookies()) {
                                if ("refreshToken".equals(cookie.getName())) {
                                        refreshToken = cookie.getValue();
                                        break;
                                }
                        }
                }

                if (refreshToken == null || refreshToken.isBlank()) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(new LoginResponseDto(
                                                        HttpStatus.UNAUTHORIZED.value(),
                                                        "Refresh token missing",
                                                        null));
                }

                try {

                        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);
                        User user = newRefreshToken.getUser();

                        // 4. Generate new access token + set cookies again
                        writeTokenCookies(
                                        response,
                                        jwtUtil.generateAccessToken(user.getUserId()),
                                        newRefreshToken.getRefreshToken());

                        return ResponseEntity.ok(
                                        new LoginResponseDto(
                                                        HttpStatus.OK.value(),
                                                        "Token refreshed successfully",
                                                        UserMapper.toResponse(user)));

                } catch (UserException ex) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(new LoginResponseDto(
                                                        HttpStatus.UNAUTHORIZED.value(),
                                                        ex.getMessage(),
                                                        null));
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
        @Operation(summary = "Get all users", description = "This API is used to fetch all users")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Users fetched successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "500", description = "Server error")
        })
        @SecurityRequirement(name = "cookieAuth")
        public ResponseEntity<ApiResponseDto<List<UserProfileResponseDto>>> getAllUsers() {

                logger.info("Request received: Get all users");

                List<UserProfileResponseDto> users = userService.getAllUsers();

                ApiResponseDto<List<UserProfileResponseDto>> response = new ApiResponseDto<>(
                                HttpStatus.OK.value(),
                                "Users fetched successfully",
                                users);

                logger.info("Response sent: All users fetched successfully");

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
        public ResponseEntity<ApiResponseDto<UserProfileResponseDto>> getUserById(
                        @Parameter(description = "User ID path variable", required = true) @PathVariable Long userId) {

                logger.debug("Get user by id request for user with id: {}", userId);

                UserProfileResponseDto user = userService.getUserById(userId);

                ApiResponseDto<UserProfileResponseDto> response = new ApiResponseDto<>(
                                HttpStatus.OK.value(),
                                "User fetched successfully",
                                user);

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
        public ResponseEntity<ApiResponseDto<UserResponseDto>> getSession(Authentication authentication) {

                Long userId = Long.parseLong(authentication.getName());

                logger.debug("Get session for user with id: {}", userId);

                User user = userService.getSessionUserById(userId);

                UserResponseDto dto = new UserResponseDto(user);

                logger.info("Session active successfully for userId: {}", userId);

                return ResponseEntity.ok(
                                new ApiResponseDto<>(
                                                HttpStatus.OK.value(),
                                                "Session active",
                                                dto));
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

    @PutMapping(value = "/profile/complete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @PostMapping("/profile-photo")
    public ResponseEntity<ProfilePhotoResponseDto> upload(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(userService.uploadProfilePhoto(file));
    }

    @GetMapping("/profile-photo")
    public ResponseEntity<ProfilePhotoResponseDto> get() {
        return ResponseEntity.ok(userService.getProfilePhoto());
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
    @GetMapping("/profile-data")
    @Operation(
            summary = "Get user profile",
            description = "Fetch logged-in user's profile using userId"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<ProfileDataResponseDto> getUserProfile(Authentication authentication) {
        String authValue = authentication.getName(); // "4"
        System.out.println("Auth Name: " + authValue);

        Long userId = Long.parseLong(authValue);

        ProfileDataResponseDto response =
                userService.getUserProfile(userId);

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

    private void writeTokenCookies(HttpServletResponse response,
                                   String accessToken,
                                   String refreshToken) {

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain("kisansetu.online")
                .sameSite("None")
                .maxAge(15 * 60)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain("kisansetu.online")
                .sameSite("None")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
        private String resolveRefreshToken(HttpServletRequest request,
                        RefreshTokenRequestDto body) {

                if (request.getCookies() != null) {
                        for (Cookie cookie : request.getCookies()) {
                                if ("refreshToken".equals(cookie.getName())) {
                                        return cookie.getValue();
                                }
                        }
                }

                if (body != null) {
                        return body.getRefreshToken();
                }

                return null;
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
    private void clearAuthCookies(HttpServletResponse response) {

        ResponseCookie clearAccessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain("kisansetu.online")
                .maxAge(0)
                .sameSite("None")
                .build();

        ResponseCookie clearRefreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain("kisansetu.online")
                .maxAge(0)
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", clearAccessCookie.toString());
        response.addHeader("Set-Cookie", clearRefreshCookie.toString());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponseDto<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDto dto
    ) {
        userService.forgotPassword(dto);

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        200,
                        "Password updated successfully",
                        null
                )
        );
    }
}
