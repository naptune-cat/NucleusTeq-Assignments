package com.example.user_directory_api.exception;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message){
        super(message);
    }
}