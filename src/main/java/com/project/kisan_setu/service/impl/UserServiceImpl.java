package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.CreateUserRequestDto;
import com.project.kisan_setu.dto.UpdateUserRequestDto;
import com.project.kisan_setu.dto.UserResponseDto;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.UserMapper;
import com.project.kisan_setu.repository.UserRepository;
import com.project.kisan_setu.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDto createUser(CreateUserRequestDto createUserRequestDto)
    {
        User user = UserMapper.toEntity(createUserRequestDto);
        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }

    @Override
        public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()->new UserException("User not found"));
    }

    @Override
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Override
    public UserResponseDto updateUserById(Long userId, UpdateUserRequestDto updateUserRequestDto){
        User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
        UserMapper.updateEntity(user,updateUserRequestDto);
        User updateUser = userRepository.save(user);
        return UserMapper.toResponse(updateUser);
    }

    @Override
    public void deleteUserById(Long userId){
        userRepository.deleteById(userId);
    }
}
