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

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/evently.in/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.registerUser(request), HttpStatus.CREATED);
    }
    

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return new ResponseEntity<>(authService.loginUser(request), HttpStatus.OK);
    }
}
