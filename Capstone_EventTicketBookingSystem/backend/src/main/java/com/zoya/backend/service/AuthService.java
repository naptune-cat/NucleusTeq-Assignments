package com.zoya.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
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
        logger.info("someone is trying to register with email: {}", request.getEmail());
        
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        // checking if email is already used
        if (existingUser.isPresent()) {
            logger.warn("registration failed because email already exists: {}", request.getEmail());
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
        // saving the user in database
        userRepository.save(user);
        logger.info("successfully registered new user: {}", request.getEmail());

        return "User registered Successfully!";
    }
    
 
        public LoginResponse loginUser(LoginRequest request) {
        logger.info("login attempt for email: {}", request.getEmail());
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        // if email isn't registered 
        if (existingUser.isEmpty()) {
            logger.warn("login failed because user not found: {}", request.getEmail());
            throw new UserNotFoundException("User not found");
        }

        User user = existingUser.get();

        // verifying the password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("login failed due to wrong password for user: {}", request.getEmail());
            throw new InvalidPasswordException("Invalid password");
        }

        /*  it will generate token using email */
        String token = jwtService.generateToken(user.getEmail(),user.getRole().name());
        logger.info("login successful and token generated for user: {}", request.getEmail());

        return new LoginResponse(token, user.getRole().name(), "Login successful");

    }

}