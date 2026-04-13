package com.example.usermanagementsystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.usermanagementsystem.model.User;
import com.example.usermanagementsystem.service.UserService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    //for retrieving all the data in our db
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    //for retrieving data of only specific id
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    // for adding a user
    @PostMapping 
    public String createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    //for deleting a user using id
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id) {
        return userService.deleteUser(id);
    }

    // for updating partial user details
    @PatchMapping("{id}")
    // taking id as Integer because it can also take null values 
    public String updatePatialDetails(@PathVariable Integer id, @RequestBody User user) {
        return userService.updatePartialDetails(id, user);
    }
    
    //for updating full user details
    @PutMapping("{id}")
    public String updateDetails(@PathVariable Integer id, @RequestBody User user) {
        return userService.updateDetails(id, user);
   }
}
