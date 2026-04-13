package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.RequestDto.*;
import com.project.kisan_setu.dto.ResponseDto.*;
import com.project.kisan_setu.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {

    SignupResponseDto signup(CreateUserRequestDto dto);
    LoginResponseDto login(LoginRequestDto dto);
    UserProfileResponseDto getUserById(Long userId);
    List<UserProfileResponseDto> getAllUsers();

    User getSessionUserById(Long userId);

    UserResponseDto updateUserById(Long userId, UpdateUserRequestDto dto);
    void deleteUserById(Long userId);
    User findByEmail(String email);
    UserProfileResponseDto completeUserProfileData(UserProfileRequestDto dto);
    void uploadProfilePhoto(MultipartFile file);

    byte[] getProfilePhoto() throws IOException;
    AccountSettingResponseDto getAccountSettings();
    AccountSettingResponseDto updateAccountSettings(AccountSettingRequestDto dto);
    ChangePasswordResponseDto changePassword(ChangePasswordRequestDto dto);
    KycStatusResponseDto getKycStatus();



}