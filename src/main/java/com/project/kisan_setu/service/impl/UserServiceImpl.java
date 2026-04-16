package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.RequestDto.ChangePasswordRequestDto;
import com.project.kisan_setu.dto.RequestDto.CreateUserRequestDto;
import com.project.kisan_setu.dto.RequestDto.LoginRequestDto;
import com.project.kisan_setu.dto.RequestDto.UpdateUserRequestDto;
import com.project.kisan_setu.dto.RequestDto.UserProfileRequestDto;
import com.project.kisan_setu.dto.ResponseDto.AccountSettingResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ChangePasswordResponseDto;
import com.project.kisan_setu.dto.ResponseDto.KycStatusResponseDto;
import com.project.kisan_setu.dto.ResponseDto.LoginResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ProfileDataResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ProfilePhotoResponseDto;
import com.project.kisan_setu.dto.ResponseDto.SignupResponseDto;
import com.project.kisan_setu.dto.ResponseDto.UserProfileResponseDto;
import com.project.kisan_setu.dto.ResponseDto.UserResponseDto;
import com.project.kisan_setu.entity.AadhaarVerification;
import com.project.kisan_setu.entity.BankAccountVerification;
import com.project.kisan_setu.entity.PanCardVerification;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.Role;
import com.project.kisan_setu.enums.UserStatus;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.UserMapper;
import com.project.kisan_setu.repository.AadhaarVerificationRepository;
import com.project.kisan_setu.repository.BankAccountVerificationRepository;
import com.project.kisan_setu.repository.PanCardVerificationRepository;
import com.project.kisan_setu.repository.UserRepository;
import com.project.kisan_setu.service.NotificationService;
import com.project.kisan_setu.service.RefreshTokenService;
import com.project.kisan_setu.service.UserService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidatorMethods validatorMethods;
    private final BankAccountVerificationRepository bankAccountVerificationRepository;
    private final RefreshTokenService refreshTokenService;
    private final PanCardVerificationRepository panCardVerificationRepository;
    private final AadhaarVerificationRepository aadhaarVerificationRepository;
    private final NotificationService notificationService;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final long MAX_SIZE = 5 * 1024 * 1024L;
    private static final List<String> ALLOWED = List.of(
            "image/jpeg",
            "image/png",
            "image/jpg");

    private final String uploadDir = System.getProperty("user.dir") + "/api";

    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.admin.secret}")
    private String adminSecret;

    @Override
    public SignupResponseDto signup(CreateUserRequestDto dto) {

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new UserException("Passwords do not match", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserException("Email already registered", HttpStatus.CONFLICT);
        }

        if (userRepository.existsByMobileNumber(dto.getMobileNumber())) {
            throw new UserException("Mobile number already registered", HttpStatus.CONFLICT);
        }

        User user = UserMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (dto.getAdminSecret() != null && dto.getAdminSecret().equals(adminSecret)) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        return new SignupResponseDto(
                201,
                "Registration successful",
                UserMapper.toResponse(user));
    }

    @Override
    public LoginResponseDto login(LoginRequestDto dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserException("Invalid email or password", HttpStatus.BAD_REQUEST));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new UserException("Invalid email or password", HttpStatus.BAD_REQUEST);
        }

        return new LoginResponseDto(
                200,
                "Login successful",
                UserMapper.toResponse(user));
    }

    @Override
    public UserProfileResponseDto getUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        return UserMapper.toDto(user);
    }

    @Override
    public List<UserProfileResponseDto> getAllUsers() {

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public User getSessionUserById(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        "User not found",
                        HttpStatus.NOT_FOUND));
    }

    @Override
    public UserResponseDto updateUserById(Long userId, UpdateUserRequestDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        UserMapper.updateEntity(user, dto, passwordEncoder);

        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void deleteUserById(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new UserException("User not found", HttpStatus.NOT_FOUND);
        }

        userRepository.deleteById(userId);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public UserProfileResponseDto completeUserProfileData(UserProfileRequestDto dto) {

        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);

        UserMapper.updateUserEntity(user, dto);
        userRepository.save(user);

        User updatedUser = userRepository.findByIdWithVerifications(userId)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        return UserMapper.toDto(updatedUser);
    }

    @Override
    public ProfilePhotoResponseDto uploadProfilePhoto(MultipartFile file) {

        validateFile(file);

        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);

        // delete old photo
        if (user.getProfilePhoto() != null && !user.getProfilePhoto().isBlank()) {
            deleteOldPhoto(user.getProfilePhoto());
        }

        String filename = buildFilename(userId, file.getOriginalFilename());
        saveFile(file, filename);

        user.setProfilePhoto(filename);
        userRepository.save(user);

        return ProfilePhotoResponseDto.builder()
                .fileName(file.getOriginalFilename())
                .filePath("/profile-photos/" + filename)
                .fileType(file.getContentType())
                .isPrimary(true)
                .build();
    }

    @Override
    public ProfilePhotoResponseDto getProfilePhoto() {

        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);

        if (user.getProfilePhoto() == null) {
            throw new RuntimeException("Profile photo not found");
        }

        Path path = Paths.get(uploadDir, "uploads", "profile-photos")
                .resolve(user.getProfilePhoto());
        System.out.println("Checking file at: " + path.toAbsolutePath());

        if (!Files.exists(path)) {
            throw new RuntimeException("File not found on disk");
        }

        String fileName = user.getProfilePhoto();

        return ProfilePhotoResponseDto.builder()
                .fileName(fileName)
                .filePath("/profile-photos/" + fileName)
                .fileType(getFileType(fileName))
                .isPrimary(true)
                .build();
    }
    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File must not be empty");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new RuntimeException("File size must not exceed 5MB");
        }

        String contentType = file.getContentType();

        if (contentType == null || ALLOWED.stream().noneMatch(contentType::equalsIgnoreCase)) {
            throw new RuntimeException("Only JPG, JPEG, PNG allowed");
        }
    }

    private String buildFilename(Long userId, String original) {
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf("."))
                : ".jpg";

        return "user_" + userId + "_" + UUID.randomUUID() + ext;
    }
    private String getFileType(String fileName) {

        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";

        return "application/octet-stream";
    }

    private void saveFile(MultipartFile file, String filename) {
        try {

            System.out.println("uploadDir value: " + uploadDir);

            Path dir = Paths.get(uploadDir, "uploads", "profile-photos")
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(dir);

            Path targetLocation = dir.resolve(filename);

            // already added before
            System.out.println("Saving file at: " + targetLocation.toAbsolutePath());

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save file");
        }
    }
    private void deleteOldPhoto(String filename) {
        try {
            Path path = Paths.get(uploadDir, "uploads", "profile-photos")
                    .resolve(filename);

            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }

    @Override
    public AccountSettingResponseDto getAccountSettings() {

        Long userId = validatorMethods.getCurrentUserId();

        User user = userRepository.findByIdWithVerifications(userId)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        return UserMapper.toAccountSettingDto(user);
    }
    @Override
    public ProfileDataResponseDto getUserProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean isVerified = Boolean.TRUE.equals(user.getSellerVerified());

        ProfilePhotoResponseDto photoDto = null;


        if (user.getProfilePhoto() != null && !user.getProfilePhoto().isEmpty()) {

            String fileName = user.getProfilePhoto();

            photoDto = ProfilePhotoResponseDto.builder()
                    .fileName(fileName)
                    .filePath(fileName)
                    .fileType(getFileType(fileName))
                    .isPrimary(true)
                    .build();
        }

        return ProfileDataResponseDto.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .farmLocation(user.getFarmLocation())
                .isVerified(isVerified)
                .profilePhoto(photoDto)
                .build();
    }





    @Override
    public ChangePasswordResponseDto changePassword(ChangePasswordRequestDto dto) {

        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new UserException("Current password incorrect", HttpStatus.UNAUTHORIZED);
        }

        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new UserException("Passwords do not match", HttpStatus.BAD_REQUEST);
        }

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new UserException("New password cannot be same", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        return ChangePasswordResponseDto.builder()
                .status(200)
                .message("Password changed successfully")
                .userId(user.getUserId())
                .build();
    }

    @Override
    public KycStatusResponseDto getKycStatus() {

        Long userId = validatorMethods.getCurrentUserId();

        Optional<AadhaarVerification> aadhaar = aadhaarVerificationRepository.findByUserUserId(userId);
        Optional<PanCardVerification> pan = panCardVerificationRepository.findByUserUserId(userId);
        Optional<BankAccountVerification> bank = bankAccountVerificationRepository.findByUserUserId(userId);

        boolean fullyVerified = aadhaar.isPresent() && pan.isPresent() && bank.isPresent();

        return KycStatusResponseDto.builder()
                .fullyVerified(fullyVerified)
                .overallMessage(fullyVerified ? "Verified" : "Pending verification")
                .build();
    }
}