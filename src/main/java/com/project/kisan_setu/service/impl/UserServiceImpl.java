package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.UserMapper;
import com.project.kisan_setu.repository.UserRepository;
import com.project.kisan_setu.security.JwtUtil;
import com.project.kisan_setu.service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public SignupResponseDto signup(CreateUserRequestDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new UserException("Passwords do not match");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserException("Email already registered");
        }

        if (userRepository.existsByMobileNumber(dto.getMobileNumber())) {
            throw new UserException("Mobile number already registered");
        }

        User user = UserMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        UserResponseDto userResponseDto = UserMapper.toResponse(user);
        return new SignupResponseDto(
                201,
                "Registration successful",
                token,
                userResponseDto
        );
    }

    @Override
    public LoginResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserException("Email not registered"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid Password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        UserResponseDto userResponseDto = UserMapper.toResponse(user);
        return new LoginResponseDto(
                200,
                "Login successful",
                token,
                userResponseDto
        );
    }



    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Override
    public UserResponseDto updateUserById(Long userId, UpdateUserRequestDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));

        UserMapper.updateEntity(user, dto,passwordEncoder);

        User updatedUser = userRepository.save(user);

        return UserMapper.toResponse(updatedUser);
    }


    @Override
    public void deleteUserById(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new UserException("User not found");
        }

        userRepository.deleteById(userId);
    }
}

