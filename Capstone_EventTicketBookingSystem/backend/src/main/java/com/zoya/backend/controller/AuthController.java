package com.zoya.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zoya.backend.dto.LoginRequest;
import com.zoya.backend.dto.LoginResponse;
import com.zoya.backend.dto.RegisterRequest;
import com.zoya.backend.service.AuthService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("got a request to register a new user: {}", request.getEmail());
        return new ResponseEntity<>(authService.registerUser(request), HttpStatus.CREATED);
    }
    

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("got a login request for user: {}", request.getEmail());
        return new ResponseEntity<>(authService.loginUser(request), HttpStatus.OK);
    }
}
