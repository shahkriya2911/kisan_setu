package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.*;
import com.project.kisan_setu.entity.AadhaarVerification;
import com.project.kisan_setu.entity.MobileVerification;
import com.project.kisan_setu.entity.RefreshToken;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.Role;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private static final long   MAX_SIZE  = 5 * 1024 * 1024L;
    private static final String[] ALLOWED  = {"image/jpeg", "image/png", "image/jpg"};
    @Value("${app.upload.dir:uploads/profile-photos}")
    private String uploadDir;

    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.admin.secret}")
    private String adminSecret;



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

        if (dto.getAdminSecret() != null && dto.getAdminSecret().equals(adminSecret)) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }
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
    public UserProfileResponseDto completeUserProfileData(UserProfileRequestDto dto) {
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

    @Override
    public UserProfileResponseDto uploadProfilePhoto(MultipartFile file) {
        logger.info("Uploading profile photo...");

        validateFile(file);

        Long userId = validatorMethods.getCurrentUserId();
        User user   = validatorMethods.validateUserById(userId);

        // Delete previous photo from disk if present
        if (user.getProfilePhoto() != null && !user.getProfilePhoto().isBlank()) {
            deleteOldPhoto(user.getProfilePhoto());
        }

        // Build unique filename and save to disk
        String filename = buildFilename(userId, file.getOriginalFilename());
        saveFileToDisk(file, filename);

        String photoUrl = baseUrl + "/" + uploadDir + "/" + filename;
        user.setProfilePhoto(photoUrl);
        userRepository.save(user);

        User updatedUser = userRepository.findByIdWithVerifications(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        logger.info("Profile photo uploaded successfully for userId: {}", userId);
        return UserProfileResponseDto.builder()
                .userId(userId)
                .profilePhotoUrl(photoUrl)
                .build();

    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("File must not be empty");

        if (file.getSize() > MAX_SIZE)
            throw new IllegalArgumentException("File size must not exceed 5 MB");

        String ct = file.getContentType();
        for (String allowed : ALLOWED)
            if (allowed.equalsIgnoreCase(ct)) return;

        throw new IllegalArgumentException("Only JPEG, PNG, JPG, WEBP images are allowed");

    }

    private String buildFilename(Long userId, String original) {
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf("."))
                : ".jpg";
        return "user_" + userId + "_" + UUID.randomUUID() + ext;
    }

    private void saveFileToDisk(MultipartFile file, String filename) {
        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("Failed to save photo: {}", e.getMessage());
            throw new RuntimeException("Could not save profile photo. Please try again.");
        }
    }

    private void deleteOldPhoto(String oldUrl) {
        try {
            String filename = oldUrl.substring(oldUrl.lastIndexOf("/") + 1);
            Files.deleteIfExists(Paths.get(uploadDir, filename));
            logger.info("Deleted old photo: {}", filename);
        } catch (IOException e) {
            logger.warn("Could not delete old photo: {}", e.getMessage());
        }
    }

    @Override
    public AccountSettingResponseDto getAccountSettings() {
        logger.info("Fetching account settings...");
        Long userId = validatorMethods.getCurrentUserId();

        User user = userRepository.findByIdWithVerifications(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserMapper.toAccountSettingDto(user);
    }
    @Override
    public AccountSettingResponseDto updateAccountSettings(AccountSettingRequestDto dto) {
        logger.info("Updating account settings...");
        Long userId = validatorMethods.getCurrentUserId();
        User user   = validatorMethods.validateUserById(userId);

        if (dto.getFullName() != null)     user.setFullName(dto.getFullName());
        if (dto.getMobileNumber() != null) user.setMobileNumber(dto.getMobileNumber());
        if (dto.getFarmLocation() != null) user.setFarmLocation(dto.getFarmLocation());

        userRepository.save(user);

        User updatedUser = userRepository.findByIdWithVerifications(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        logger.info("Account settings updated successfully.");
        return UserMapper.toAccountSettingDto(user);
    }

    @Override
    public ChangePasswordResponseDto changePassword(ChangePasswordRequestDto dto) {
        Long userId = validatorMethods.getCurrentUserId();
        User user=validatorMethods.validateUserById(userId);
        logger.info("Changing/Updating Password...");

        if(!passwordEncoder.matches(dto.getCurrentPassword(),user.getPassword())){
            throw new UserException("Current Password is Incorrect");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new UserException("New password and confirm password do not match");
        }
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new UserException("New password cannot be same as current password");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        logger.info("Password changed successfully for userId: {}", userId);

        return ChangePasswordResponseDto.builder()
                .status(200)
                .message("Password changed successfully")
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .passwordChangedAt(LocalDateTime.now())
                .build();
    }

}

