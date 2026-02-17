package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.entity.UserRole;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static User toEntity(CreateUserRequestDto createUserRequestDto) {

        User user = new User();
        user.setUserFullName(createUserRequestDto.getUserFullName());
        user.setUserEmail(createUserRequestDto.getUserEmail());
        user.setUserPhoneNumber(createUserRequestDto.getUserPhoneNumber());
        user.setUserPassword(createUserRequestDto.getUserPassword());
        user.setUserCreatedAt(createUserRequestDto.getUserCreatedAt());
        user.setUserAddress(createUserRequestDto.getUserAddress());
        if (createUserRequestDto.getRoles() != null && !createUserRequestDto.getRoles().isEmpty()) {

            List<UserRole> userRoleList = createUserRequestDto.getRoles()
                    .stream()
                    .map(roleEnum -> {
                        UserRole userRole = new UserRole();
                        userRole.setUserRoles(roleEnum);
                        userRole.setUser(user);   // IMPORTANT 🔥
                        return userRole;
                    })
                    .collect(Collectors.toList());

            user.setUserRoles(userRoleList);
        }

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
