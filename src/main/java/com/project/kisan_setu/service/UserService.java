package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.CreateUserRequestDto;
import com.project.kisan_setu.dto.UpdateUserRequestDto;
import com.project.kisan_setu.dto.UserResponseDto;
import com.project.kisan_setu.dto.LoginRequestDto;
import com.project.kisan_setu.entity.User;

import java.util.List;

public interface UserService {

    UserResponseDto signup(CreateUserRequestDto dto);
    UserResponseDto login(LoginRequestDto dto);
    User getUserById(Long userId);
    List<User> getAllUsers();
    UserResponseDto updateUserById(Long userId, UpdateUserRequestDto dto);
    void deleteUserById(Long userId);
}
