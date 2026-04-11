package com.example.usermanagementsystem.service;

// imports ---
import org.springframework.stereotype.Service;

import com.example.usermanagementsystem.exception.UserIdAlreadyExistsException;
import com.example.usermanagementsystem.exception.UserNotFoundException;
import com.example.usermanagementsystem.model.User;
import com.example.usermanagementsystem.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    //constructor injection
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

    public String createUser(User user) {
        User u = userRepository.getUserById(user.getId());
        if (u != null) {
            throw new UserIdAlreadyExistsException("User Id already exists: " + user.getId());
        }
        userRepository.saveUser(user);
        return "User added successfully";
    }
    
    public String deleteUser(int id) {
        if (!userRepository.deleteUser(id)) {
            throw new UserNotFoundException("No such user exists");
        }
        return "User deleted Successfully";
    }
}
