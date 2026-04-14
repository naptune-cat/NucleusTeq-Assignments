package com.example.user_directory_api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.user_directory_api.exception.UserNotFoundException;
import com.example.user_directory_api.model.User;
import com.example.user_directory_api.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public User getUserById(Integer id) {
        User user = userRepository.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException("No such user exists");
        }
        return user;
    }

    public String deleteUserById(Integer id, Boolean confirm) {
        if (!confirm || confirm ==null) {
            return "Confirmation required";
        }

        Boolean isUserDeleted = userRepository.deleteUser(id);

        if (!isUserDeleted) {
            throw new UserNotFoundException("No such user exists");
        }
        return "User deleted Successfully";
    }

    public List<User> searchUser(String name, String role, Integer age) {
        List<User> allUsers = userRepository.getAllUsers();

        //if all teh parameters are empty we will directly return all Users
        if ((name == null || name.isBlank()) && 
            (role == null || role.isBlank()) && 
            (age == null || age == 0)) {
                return allUsers;
        }
        
        //we will store our multiple matched users inside matchedUser List
        List<User> matchedUsers = new ArrayList<>();

        //iterating through each user and adding matched user in list
        for (User user : allUsers) {
            // this flag will help us know which user should be added to our matched user list
            Boolean matches = true;

            if (name != null && !name.equalsIgnoreCase(user.getName())) {
                matches = false;
            }
            if (role != null && !role.equalsIgnoreCase(user.getRole())) {
                matches = false;
            }
            if (age != null && !age.equals(user.getAge())) {
                matches = false;
            }

            if (matches) {
                matchedUsers.add(user);
            }
        }

        if (matchedUsers.isEmpty()) {
            throw new UserNotFoundException("No matching users found");
        }
        return matchedUsers;
    }
}
