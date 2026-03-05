package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.RefreshToken;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.mapper.UserMapper;
import com.project.kisan_setu.repository.RefreshTokenRepository;
import com.project.kisan_setu.repository.UserRepository;
import com.project.kisan_setu.security.JwtUtil;
import com.project.kisan_setu.service.RefreshTokenService;
import com.project.kisan_setu.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController //handling of rest apis
@RequestMapping("api/users") //api starts with /users
public class UserController {

    //constructor dependency injection
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, JwtUtil jwtUtil, RefreshTokenService refreshTokenService, RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, RefreshTokenService refreshTokenService1) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService1;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(
            @Valid @RequestBody CreateUserRequestDto dto,
            HttpServletResponse response) {

        logger.debug("Signup request for email: {}", dto.getEmail());

        SignupResponseDto responseDto = userService.signup(dto);

        // Fetch saved user from DB
        User user = userService.findByEmail(
                responseDto.getData().getEmail()
        );

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getUserId());
        String refreshToken = refreshTokenService
                .createRefreshToken(user)
                .getRefreshToken();

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false)              // MUST be false on HTTP
                .path("/")
                .maxAge(jwtUtil.getAccessExpiration() / 1000)
                .sameSite("Lax")            // Change from Strict
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)              // MUST be false on HTTP
                .path("/")
                .maxAge(jwtUtil.getRefreshExpiration() / 1000)
                .sameSite("Lax")            // Change from Strict
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        logger.info("Signup successful for email: {}", user.getEmail());

        return ResponseEntity.ok(responseDto);
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto dto,
            HttpServletResponse response) {

        logger.debug("Login request for email: {}", dto.getEmail());

        LoginResponseDto responseDto = userService.login(dto);

        // Fetch authenticated user
        User user = userService.findByEmail(
                responseDto.getData().getEmail()
        );

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getUserId());
        String refreshToken = refreshTokenService
                .createRefreshToken(user)
                .getRefreshToken();

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false)              //  MUST be false on HTTP
                .path("/")
                .maxAge(jwtUtil.getAccessExpiration() / 1000)
                .sameSite("Lax")            // Change from Strict
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)              // MUST be false on HTTP
                .path("/")
                .maxAge(jwtUtil.getRefreshExpiration() / 1000)
                .sameSite("Lax")            // Change from Strict
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        logger.info("Login successful for email: {}", user.getEmail());

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(
            @RequestBody RefreshTokenRequestDto request) {

        return ResponseEntity.ok(
                refreshTokenService.refreshAccessToken(request.getRefreshToken())
        );
    }

    //LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestBody RefreshTokenRequestDto request) {

        refreshTokenService.revokeToken(request.getRefreshToken());

        return ResponseEntity.ok("Logged out successfully");
    }

    //get all users
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<UserResponseDto>>> getAllUsers() {
        logger.info("Get all users request");
        List<UserResponseDto> users = userService.getAllUsers()//call get all users method and convert it into list
                .stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());

        //inserting values in api response
        ApiResponseDto<List<UserResponseDto>> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "Users fetched successfully",
                users
        );

        logger.info("All users fetched successfully");
        //response send to frontend
        return ResponseEntity.ok(response);
    }


    //get user by id
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getUserById(
            @PathVariable Long userId) {
        logger.debug("Get user by id request for user with id : {}",userId);
        User user = userService.getUserById(userId); //get user by id method call

        //inserting values in api response
        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "User fetched successfully",
                new UserResponseDto(user)
        );

        //response send to frontend
        logger.info("User with id :{} fetched successfully",userId);
        return ResponseEntity.ok(response);
    }

    //update user by id
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequestDto dto) { //json from user
        logger.debug("Update user by id request for user with id : {}",userId);

        UserResponseDto updatedUser = userService.updateUserById(userId, dto); //update method call

        //inserting values in api response
        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "User updated successfully",
                updatedUser
        );

        logger.info("User updated successfully");
        //response send to frontend
        return ResponseEntity.ok(response);
    }

    //delete by user id
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteUser(
            @PathVariable Long userId) {
        logger.debug("Delete user with id request for user with id : {}",userId);
        userService.deleteUserById(userId);//delete method call

        //inserting values in api response
        ApiResponseDto<Void> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "User deleted successfully",
                null
        );
        logger.info("User deleted successfully");
        //response send to frontend
        return ResponseEntity.ok(response);
    }
}
