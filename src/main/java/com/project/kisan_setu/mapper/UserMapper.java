package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.UpdateUserRequestDto;
import com.project.kisan_setu.dto.UserResponseDto;
import com.project.kisan_setu.dto.CreateUserRequestDto;
import com.project.kisan_setu.entity.User;
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
        return dto;
    }
}
