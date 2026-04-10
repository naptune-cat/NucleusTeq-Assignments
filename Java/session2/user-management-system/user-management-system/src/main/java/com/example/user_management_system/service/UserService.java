package com.example.user_management_system.service;

// imports ---
import org.springframework.stereotype.Service;

//repository 
import com.example.user_management_system.repository.UserRepository;

//exception class
import com.example.user_management_system.exception.UserNotFoundException;

import java.util.List;

//model
import com.example.user_management_system.model.User;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }
    
    public User getUserById(int id) {
        User user = userRepository.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        return user;
    }

    public User createUser(User user) {
        return userRepository.saveUser(user);
    }
}
