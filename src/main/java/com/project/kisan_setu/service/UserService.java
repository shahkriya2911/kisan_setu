package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.RequestDto.*;
import com.project.kisan_setu.dto.ResponseDto.*;
import com.project.kisan_setu.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    SignupResponseDto signup(CreateUserRequestDto dto);
    LoginResponseDto login(LoginRequestDto dto);
    User getUserById(Long userId);
    List<User> getAllUsers();
    UserResponseDto updateUserById(Long userId, UpdateUserRequestDto dto);
    void deleteUserById(Long userId);
    User findByEmail(String email);
    UserProfileResponseDto completeUserProfileData(UserProfileRequestDto dto);
    UserProfileResponseDto uploadProfilePhoto(MultipartFile file);
    AccountSettingResponseDto getAccountSettings();
    AccountSettingResponseDto updateAccountSettings(AccountSettingRequestDto dto);
    ChangePasswordResponseDto changePassword(ChangePasswordRequestDto dto);
    KycStatusResponseDto getKycStatus();



}
