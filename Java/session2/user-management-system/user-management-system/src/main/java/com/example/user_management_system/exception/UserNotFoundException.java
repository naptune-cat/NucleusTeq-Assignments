package com.example.user_management_system.exception;

//making custom exception
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message){
        super(message);
    }
}
