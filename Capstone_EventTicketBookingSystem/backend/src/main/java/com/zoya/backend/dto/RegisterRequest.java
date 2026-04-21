package com.zoya.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min=3, message = "should have atleast 3 charachters")
    private String name;

    @NotBlank( message="Email is required")
    @Email( message = "Enter valid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,12}$",
                message = "Password must contain - 8 to 12 chars, atleast one  UpperCase , one special character"
    )
    private String password;


    // getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // the encoder receives plain password and then in sevice layer we encode it and save the encoded version in our db

    public String getPassword() {
        return password;
    }


    // setters

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
