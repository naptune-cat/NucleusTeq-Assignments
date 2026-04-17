package com.example.dto;


import com.example.enums.TodoStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class RequestDTO {

    // creating private data members with validation
    @NotNull(message = "title required")
    @Size(min=3,message = "Title must be atleast 3 characters")
    private String title;

    @NotBlank(message = "description requied")
    private String description;

    private TodoStatus status;

    public RequestDTO(String title, String description, TodoStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    //empty constuctor required by JPA to instantiate the objects
    public RequestDTO(){}
    
    // setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TodoStatus status) {
        this.status = status;
    }

    // getters
    public String getTitle() {
        return title;
    }

    public String geDescription() {
        return description;
    }

    public TodoStatus getStatus() {
        return status;
    }
}
