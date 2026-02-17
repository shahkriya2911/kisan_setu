package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.CreateUserRequestDto;
import com.project.kisan_setu.dto.UpdateUserRequestDto;
import com.project.kisan_setu.dto.UserResponseDto;
import com.project.kisan_setu.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponseDto createUser(CreateUserRequestDto userRequestDto);
    User getUserById(Long userId);

    List<User> getAllUsers();

    UserResponseDto updateUserById(Long userId, UpdateUserRequestDto updateUserRequestDto);

    void deleteUserById(Long userId);
}
