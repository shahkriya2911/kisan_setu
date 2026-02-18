package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> register(
            @Valid @RequestBody CreateUserRequestDto dto) {

        return ResponseEntity.ok(userService.signup(dto));
    }
    @PostMapping("/login")
    public LoginResponseDto login(
            @RequestBody LoginRequestDto dto) {

        String token = userService.login(dto);

        return new LoginResponseDto(
                token,
                dto.getEmail(),
                "Login Successful"
        );
    }


    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId){
        return userService.getUserById(userId);
    }

    @PutMapping("/{userId}")
    public UserResponseDto updateUserById(@PathVariable Long userId,@RequestBody UpdateUserRequestDto updateUserRequestDto){
        return userService.updateUserById(userId,updateUserRequestDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId){
        userService.deleteUserById(userId);
    }

}
