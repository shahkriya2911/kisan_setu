package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.security.JwtUtil;
import com.project.kisan_setu.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> signup(
            @Valid @RequestBody CreateUserRequestDto dto) {

        UserResponseDto userDto = userService.signup(dto);
        String token = jwtUtil.generateToken(userDto.getEmail());

        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>(
                201,
                "Registration successful",
                token,
                userDto
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> login(
            @Valid @RequestBody LoginRequestDto dto) {

        UserResponseDto userDto = userService.login(dto);
        String token = jwtUtil.generateToken(dto.getEmail());

        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>(
                200,
                "Login successful",
                token,
                userDto
        );

        return ResponseEntity.ok(response);
    }



    // ================= GET ALL USERS =================
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<UserResponseDto>>> getAllUsers() {

        List<UserResponseDto> users = userService.getAllUsers()
                .stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());

        ApiResponseDto<List<UserResponseDto>> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "Users fetched successfully",
                null,
                users
        );

        return ResponseEntity.ok(response);
    }

    // ================= GET USER BY ID =================
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getUserById(
            @PathVariable Long userId) {

        User user = userService.getUserById(userId);

        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "User fetched successfully",
                null,
                new UserResponseDto(user)
        );

        return ResponseEntity.ok(response);
    }

    // ================= UPDATE USER =================
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequestDto dto) {

        UserResponseDto updatedUser = userService.updateUserById(userId, dto);

        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "User updated successfully",
                null,
                updatedUser
        );

        return ResponseEntity.ok(response);
    }

    // ================= DELETE USER =================
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteUser(
            @PathVariable Long userId) {

        userService.deleteUserById(userId);

        ApiResponseDto<Void> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                "User deleted successfully",
                null,
                null
        );

        return ResponseEntity.ok(response);
    }
}

