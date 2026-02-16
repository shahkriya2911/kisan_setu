package com.project.kisan_setu.service;

import com.project.kisan_setu.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    User getUserById(Long userId);

    List<User> getAllUsers();

    User updateUserById(Long userId, User user);

    void deleteUserById(Long userId);
}
