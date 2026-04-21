package com.zoya.backend.dto;

public class LoginResponse {
    private String token;

    //message is for showing readable message to frontend

    private String message;

    //constructors

    public LoginResponse() {

    }

    public LoginResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }

    //setters 
    public void setToken(String token) {
        this.token = token;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    //getters

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
        
    }
}
