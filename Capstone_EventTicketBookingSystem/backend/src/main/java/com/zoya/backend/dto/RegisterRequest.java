package com.zoya.backend.dto;

public class RegisterRequest {
    private String name;
    private String email;
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
