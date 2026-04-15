package com.example.user_directory_api.contoller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_directory_api.model.User;
import com.example.user_directory_api.service.UserService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //using RequestEntity to have more control over HTTP response
    @GetMapping
    public  ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        //ok in status code is 200 which means search successful
        return ResponseEntity.ok(users);
        
        
    }
    

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
        @RequestParam(required = false) String name, 
        @RequestParam(required = false) String role,
        @RequestParam(required = false) Integer age) {
        List<User> users = userService.searchUsers(name, role, age);
        return ResponseEntity.ok(users);
    }
    
    //for adding user
    @PostMapping("/submit")
    public ResponseEntity<String> addUser(@RequestBody User user) {
        userService.submitData(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Added Successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id, @RequestParam(required = false) Boolean confirm) {
        userService.deleteUserById(id,confirm);
        return ResponseEntity.ok("User deleted successfully");
    }
}
