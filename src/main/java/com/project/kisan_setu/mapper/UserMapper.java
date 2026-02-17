package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.User;

public class UserMapper {

    public static User toEntity(CreateUserRequestDto createUserRequestDto) {

        User user = new User();
        user.setUserFullName(createUserRequestDto.getUserFullName());
        user.setUserEmail(createUserRequestDto.getUserEmail());
        user.setUserPhoneNumber(createUserRequestDto.getUserPhoneNumber());
        user.setUserPassword(createUserRequestDto.getUserPassword());
        user.setUserCreatedAt(createUserRequestDto.getUserCreatedAt());
        user.setUserAddress(createUserRequestDto.getUserAddress());
        return user;
    }

    public static void updateEntity(User user, UpdateUserRequestDto updateUserRequestDto) {

        if (updateUserRequestDto.getUserFullName() != null)
            user.setUserFullName(updateUserRequestDto.getUserFullName());

        if (updateUserRequestDto.getUserEmail() != null)
            user.setUserEmail(updateUserRequestDto.getUserEmail());

        if (updateUserRequestDto.getUserPhoneNumber() != null)
            user.setUserPhoneNumber(updateUserRequestDto.getUserPhoneNumber());

        if (updateUserRequestDto.getUserPassword() != null)
            user.setUserPassword(updateUserRequestDto.getUserPassword());

        if (updateUserRequestDto.getUserCreatedAt() != null)
            user.setUserCreatedAt(updateUserRequestDto.getUserCreatedAt());

        if (updateUserRequestDto.getUserAddress() != null)
            user.setUserAddress(updateUserRequestDto.getUserAddress());
    }

    public static UserResponseDto toResponse(User user) {

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUserId(user.getUserId());
        userResponseDto.setUserFullName(user.getUserFullName());
        userResponseDto.setUserEmail(user.getUserEmail());
        userResponseDto.setUserPhoneNumber(user.getUserPhoneNumber());
        userResponseDto.setUserCreatedAt(user.getUserCreatedAt());
        userResponseDto.setUserAddress(user.getUserAddress());

        return userResponseDto;
    }
}
