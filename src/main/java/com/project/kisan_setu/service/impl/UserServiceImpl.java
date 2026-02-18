package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.CreateUserRequestDto;
import com.project.kisan_setu.dto.LoginRequestDto;
import com.project.kisan_setu.dto.UpdateUserRequestDto;
import com.project.kisan_setu.dto.UserResponseDto;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.UserMapper;
import com.project.kisan_setu.repository.BuyingRequirementRepository;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.repository.UserRepository;
import com.project.kisan_setu.security.JwtUtil;
import com.project.kisan_setu.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public UserServiceImpl(UserRepository userRepository, ListingRepository sellerAddCropRepository, BuyingRequirementRepository buyingRequirementRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;

        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public String signup(CreateUserRequestDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        if (userRepository.existsByMobileNumber(dto.getMobileNumber())) {
            throw new RuntimeException("Mobile number already registered");
        }

        User user = UserMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        return "Registration successful";
    }

    @Override
    public String login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        String token = jwtUtil.generateToken(user.getEmail());

        return token;
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

//    public String getUserRole(Long userId) {
//
//        // Optional: Check user exists
//        userRepository.findById(userId)
//                .orElseThrow(() -> new UserException("User not found"));
//
//        boolean isSeller = sellerAddCropRepository.existsBySellerUserId(userId);
//        boolean isBuyer = buyingRequirementRepository.existsByBuyerUserId(userId);
//
//        if (isSeller && isBuyer) return "SELLER & BUYER";
//        if (isSeller) return "SELLER";
//        if (isBuyer) return "BUYER";
//
//        return "NEW_USER";
//    }
}
