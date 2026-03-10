package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    SignupResponseDto signup(CreateUserRequestDto dto);
    LoginResponseDto login(LoginRequestDto dto);
    User getUserById(Long userId);
    List<User> getAllUsers();
    UserResponseDto updateUserById(Long userId, UpdateUserRequestDto dto);
    void deleteUserById(Long userId);
    User findByEmail(String email);
    UserProfileResponseDto updateUserProfileData(UserProfileRequestDto dto);



}
