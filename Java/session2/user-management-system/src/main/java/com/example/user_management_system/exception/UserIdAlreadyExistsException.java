package com.example.user_management_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserIdAlreadyExistsException extends RuntimeException {
    public UserIdAlreadyExistsException(String message){
        super(message);
    }
}
