package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.entity.User;
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
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
        public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
    }

    @Override
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Override
    public User updateUserById(Long userId, User user){
        User updateUser = userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
        updateUser.setUserFullName(user.getUserFullName());
        updateUser.setUserEmail(user.getUserEmail());
        updateUser.setUserPhoneNumber(user.getUserPhoneNumber());
        updateUser.setUserAddress(user.getUserAddress());
        return userRepository.save(updateUser);
    }

    @Override
    public void deleteUserById(Long userId){
        userRepository.deleteById(userId);
    }
}
