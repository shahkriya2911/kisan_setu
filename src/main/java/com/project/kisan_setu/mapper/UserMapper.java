package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.RequestDto.CreateUserRequestDto;
import com.project.kisan_setu.dto.RequestDto.UpdateUserRequestDto;
import com.project.kisan_setu.dto.RequestDto.UserProfileRequestDto;
import com.project.kisan_setu.dto.ResponseDto.AccountSettingResponseDto;
import com.project.kisan_setu.dto.ResponseDto.UserProfileResponseDto;
import com.project.kisan_setu.dto.ResponseDto.UserResponseDto;
import com.project.kisan_setu.entity.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.format.DateTimeFormatter;

public class UserMapper {


    public static User toEntity(CreateUserRequestDto dto) {
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setMobileNumber(dto.getMobileNumber());
        user.setPassword(dto.getPassword()); // encoding happens in service
        return user;
    }


    public static void updateEntity(User user, UpdateUserRequestDto dto, PasswordEncoder passwordEncoder) {
        if (dto.getUserFullName() != null) user.setFullName(dto.getUserFullName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getUserPhoneNumber() != null) user.setMobileNumber(dto.getUserPhoneNumber());
        if (dto.getUserPassword() != null) user.setPassword(passwordEncoder.encode(dto.getUserPassword()));
    }


    public static UserResponseDto toResponse(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setMobileNumber(user.getMobileNumber());
        if (user.getCreatedAt() != null) {
            dto.setUserCreatedAt(user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        dto.setRole(user.getRole());
        return dto;
    }

    public static UserProfileResponseDto toDto(User user)
    {
        MobileVerification mobile = user.getMobileVerification();
        AadhaarVerification aadhaar = user.getAadhaarVerification();
        BankAccountVerification bank = user.getBankAccountVerification();
        PanCardVerification pan = user.getPanCardVerification();

        return UserProfileResponseDto.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .profilePhotoUrl(user.getProfilePhoto())
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth())
                .mobileNumber(user.getMobileNumber())
                .bankName(user.getBankName())
                .accountNumber(user.getAccountNumber())
                .ifscCode(user.getIfscCode())
                .farmSize(user.getFarmSize())
                .primaryCrops(user.getPrimaryCrops())
                .farmLocation(user.getFarmLocation())
                .yearsOfExperience(user.getYearsOfExperience())

                .mobileVerified(mobile != null && mobile.isVerified() ? true : null)
                .aadhaarVerified(aadhaar != null && aadhaar.isVerified() ? true : null)
                .bankAccountVerified(bank != null && bank.isVerified() ? true : null)
                .panCardVerified(pan != null && pan.isVerified() ? true : null )
                .build();
    }
    public static void updateUserEntity(User user, UserProfileRequestDto request) {
        if(request.getDateOfBirth()!= null)
            user.setDateOfBirth(request.getDateOfBirth());
        if (request.getBankName() != null)
            user.setBankName(request.getBankName());
        if (request.getAccountNumber() != null)
            user.setAccountNumber(request.getAccountNumber());
        if (request.getIfscCode() != null)
            user.setIfscCode(request.getIfscCode());
        if (request.getUpiId() != null)
            user.setUpiId(request.getUpiId());
        if (request.getFarmSize() != null)
            user.setFarmSize(request.getFarmSize());
        if (request.getPrimaryCrops() != null)
            user.setPrimaryCrops(request.getPrimaryCrops());
        if (request.getFarmLocation() != null)
            user.setFarmLocation(request.getFarmLocation());
        if (request.getYearsOfExperience() != null)
            user.setYearsOfExperience(request.getYearsOfExperience());
    }

    public static AccountSettingResponseDto toAccountSettingDto(User user) {

        return AccountSettingResponseDto.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .profilePhotoUrl(user.getProfilePhoto())
                .farmLocation(user.getFarmLocation())
                .build();
    }


}
