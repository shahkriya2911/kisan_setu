package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.User;

import java.time.LocalDateTime;

public class UserMapper {

    public static User toEntity(CreateUserRequestDto createUserRequestDto) {

        User user = new User();
        user.setFullName(createUserRequestDto.getFullName());
        user.setEmail(createUserRequestDto.getEmail());
        user.setMobileNumber(createUserRequestDto.getMobileNumber());
        user.setPassword(createUserRequestDto.getPassword());
        user.setCreatedAt(LocalDateTime.now());

        return user;
    }

    public static void updateEntity(User user, UpdateUserRequestDto updateUserRequestDto) {

        if (updateUserRequestDto.getUserFullName() != null)
            user.setFullName(updateUserRequestDto.getUserFullName());

        if (updateUserRequestDto.getEmail() != null)
            user.setEmail(updateUserRequestDto.getEmail());

        if (updateUserRequestDto.getUserPhoneNumber() != null)
            user.setMobileNumber(updateUserRequestDto.getUserPhoneNumber());

        if (updateUserRequestDto.getUserPassword() != null)
            user.setPassword(updateUserRequestDto.getUserPassword());




    }

    public static UserResponseDto toResponse(User user) {

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUserId(user.getUserId());
        userResponseDto.setFullName(user.getFullName());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setMobileNumber(user.getMobileNumber());
        userResponseDto.setUserCreatedAt(String.valueOf(user.getCreatedAt()));


        return userResponseDto;
    }
}
