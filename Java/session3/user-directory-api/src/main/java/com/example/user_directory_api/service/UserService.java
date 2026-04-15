package com.example.user_directory_api.service;

import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Service;

import com.example.user_directory_api.exception.InvalidInputException;
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
        if (confirm == null || !confirm) {
            throw new InvalidInputException("Confirmation required as true");
        }
        if (id <= 0) {
            throw new InvalidInputException("Id cannot be 0 or less");
        }
        boolean isUserDeleted = userRepository.deleteUser(id);

        if (!isUserDeleted) {
            throw new UserNotFoundException("No such user exists");
        }
        return "User deleted Successfully";
    }

    public List<User> searchUsers(String name, String role, Integer age) {
        List<User> allUsers = userRepository.getAllUsers();

        //if all the parameters are empty we will directly return all Users
        if ((name == null || name.isBlank()) &&
            (role == null || role.isBlank()) &&
            (age == null)) {
            return allUsers;
        }
        
        if (age != null && age <= 0) {
            throw new InvalidInputException("Age must be greater than 0");
        }

        //we will store our multiple matched users inside matchedUser List
        List<User> matchedUsers = new ArrayList<>();

        //iterating through each user and adding matched user in list
        for (User user : allUsers) {
            // this flag will help us know which user should be added to our matched user list
            boolean matches = true;

            if (name != null && !name.isBlank() && !name.equalsIgnoreCase(user.getName())) {
                matches = false;
            }
            if (role != null && !role.isBlank() && !role.equalsIgnoreCase(user.getRole())) {
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

    //for submit api
    public String submitData(User user) {

        if (user == null){
            throw new InvalidInputException("Input body cannot be empty");
        }
        if(user.getName() == null || user.getName().isBlank()){
            throw new InvalidInputException("Invalid name input - cannot be null or blank"); 
        }
        if(user.getRole() == null || user.getRole().isBlank()){
            throw new InvalidInputException("Invalid role input - cannot be null or blank");  
        }
        if (user.getAge() == null || user.getAge() <= 0) {
            throw new InvalidInputException("Invalid age input - cannot be less than 1 or null");
        }
        if (user.getId() == null || user.getId() <= 0) {
            throw new InvalidInputException("Invalid id input - cannot be null or less than 1");
        }

        //checking if the given id already exists
        User existingUser = userRepository.getUserById(user.getId());

        if (existingUser != null) {
            throw new InvalidInputException("Id already exists. Try with a valid id");
        }

        boolean isAdded = userRepository.addUser(user);
        if (!isAdded) {
            throw new RuntimeException("User could not be added");
        }
       
        return "User added successfully";
    }
}
