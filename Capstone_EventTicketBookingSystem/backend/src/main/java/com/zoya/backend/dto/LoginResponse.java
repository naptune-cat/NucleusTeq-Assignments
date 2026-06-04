package com.zoya.backend.dto;

public class LoginResponse {
    private String token;

    private String role;
    //message is for showing readable message to frontend

    private String message;

    //constructors

    public LoginResponse() {

    }

    public LoginResponse(String token, String role, String message) {
        this.token = token;
        this.role = role;
        this.message = message;
    }

    //setters 
    public void setToken(String token) {
        this.token = token;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    //getters

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public String getMessage() {
        return message;
        
    }
}