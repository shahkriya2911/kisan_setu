package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.CreateUserRequestDto;
import com.project.kisan_setu.dto.UpdateUserRequestDto;
import com.project.kisan_setu.dto.UserResponseDto;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponseDto createUser(@RequestBody CreateUserRequestDto createUserRequestDto){
        return userService.createUser(createUserRequestDto);
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
