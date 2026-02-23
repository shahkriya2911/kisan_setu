package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.security.JwtUtil;
import com.project.kisan_setu.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController //handling of rest apis
@RequestMapping("/users") //api starts with /users
public class UserController {

    //constructor dependency injection
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    //signup
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(
            @Valid @RequestBody CreateUserRequestDto dto) { //json data
        logger.debug("Signup request for user with email : {}",dto.getEmail());
        SignupResponseDto userDto = userService.signup(dto); //signup method will be called and response will be stored

        logger.info("Signup successful for user with email : {}",dto.getEmail());
        //response send to frontend
        return ResponseEntity.ok(userDto);
    }

    //login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto dto) { //json from user
        logger.debug("Login request for user with email : {}",dto.getEmail());
        LoginResponseDto userDto = userService.login(dto); //login method will be called and response will be stored

        logger.info("Login successful for user with email : {}",dto.getEmail());
        //response send to frontend
        return ResponseEntity.ok(userDto);
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

