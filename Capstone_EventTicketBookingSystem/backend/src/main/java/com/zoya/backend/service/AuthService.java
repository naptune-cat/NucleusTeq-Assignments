package com.zoya.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zoya.backend.dto.LoginRequest;
import com.zoya.backend.dto.LoginResponse;
import com.zoya.backend.dto.RegisterRequest;
import com.zoya.backend.entity.User;
import com.zoya.backend.enums.UserRole;
import com.zoya.backend.exception.InvalidPasswordException;
import com.zoya.backend.exception.UserAlreadyExistsException;
import com.zoya.backend.exception.UserNotFoundException;
import com.zoya.backend.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    //constructors

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    
    public String registerUser(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        /* calling password encoder method to encrypt the password and save it to db */
        
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        /* for making role as organizer i am using a code if the user enters PartyPalooza9988 during registering that user will have role set as organizer */

        if ("PartyPalooza9988".equals(request.getOrganizerCode())) {
            user.setRole(UserRole.ORGANIZER);
        } else {
            user.setRole(UserRole.CUSTOMER);
        }
        user.setPhone(request.getPhone());
        userRepository.save(user);

        return "User registered Successfully!";
    }
    
 
    public LoginResponse loginUser(LoginRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        // if email isn't registered 
        if (existingUser.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        User user = existingUser.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }

        /*  it will generate token using email */
        String token = jwtService.generateToken(user.getEmail(),user.getRole().name());

        return new LoginResponse(token, "Login successful");

    }

}
