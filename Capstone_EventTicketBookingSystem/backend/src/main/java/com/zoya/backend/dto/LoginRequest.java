package com.zoya.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,12}$",
                message = "Password must contain - 8 to 12 chars, atleast one  UpperCase , one special character"
    )
    private String password;

    //setters 

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // getters

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
