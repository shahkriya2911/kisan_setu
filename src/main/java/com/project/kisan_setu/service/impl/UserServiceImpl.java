package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.AadhaarVerification;
import com.project.kisan_setu.entity.MobileVerification;
import com.project.kisan_setu.entity.RefreshToken;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.UserMapper;
import com.project.kisan_setu.repository.AadhaarVerificationRepository;
import com.project.kisan_setu.repository.MobileVerificationRepository;
import com.project.kisan_setu.repository.RefreshTokenRepository;
import com.project.kisan_setu.repository.UserRepository;
import com.project.kisan_setu.security.JwtUtil;
import com.project.kisan_setu.service.RefreshTokenService;
import com.project.kisan_setu.service.UserService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidatorMethods validatorMethods;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final JavaMailSender javaMailSender;
    private final MobileVerificationRepository mobileVerificationRepository;
    private final AadhaarVerificationRepository aadhaarVerificationRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    @Override
    public SignupResponseDto signup(CreateUserRequestDto dto) {
        logger.info("Signing up for user...");
        logger.info("Checking validations...");
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
        UserResponseDto userResponseDto = UserMapper.toResponse(user);
        logger.info("Signup success...");
        return new SignupResponseDto(
                201,
                "Registration successful",
                userResponseDto
        );
    }

    @Override
    public LoginResponseDto login(LoginRequestDto dto) {
        logger.info("Logging in for user...");
        logger.info("Checking validations for user...");
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserException("Email not registered"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid Password");
        }
        UserResponseDto userResponseDto = UserMapper.toResponse(user);
        logger.info("Login success...");
        return new LoginResponseDto(
                200,
                "Login successful",
                userResponseDto
        );
    }



    @Override
    public User getUserById(Long userId) {
        logger.info("Getting user by id...");
        logger.info("Fetching user by id success...");
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));
    }


    @Override
    public List<User> getAllUsers() {
        logger.info("Getting all users...");
        logger.info("Fetching all users success...");
        return userRepository.findAll();
    }


    @Override
    public UserResponseDto updateUserById(Long userId, UpdateUserRequestDto dto) {
        logger.info("Updating user with id...");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));

        UserMapper.updateEntity(user, dto,passwordEncoder);

        User updatedUser = userRepository.save(user);
        logger.info("Update user success...");
        return UserMapper.toResponse(updatedUser);
    }


    @Override
    public void deleteUserById(Long userId) {
        logger.info("Deleting user by id...");
        if (!userRepository.existsById(userId)) {
            throw new UserException("User not found");
        }
        logger.info("User deleted by id...");
        userRepository.deleteById(userId);
    }

    @Override
    public User findByEmail(String email) {
        logger.info("Finding user by email...");
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserProfileResponseDto updateUserProfileData(UserProfileRequestDto dto) {
        logger.info("Updating User profile data...");
        logger.info("Checking user validations...");
        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);

        UserMapper.updateUserEntity(user,dto);
        userRepository.save(user);

        User updatedUser = userRepository.findByIdWithVerifications(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        logger.info("Profile data updated success...");
        return UserMapper.toDto(updatedUser);
    }

}

