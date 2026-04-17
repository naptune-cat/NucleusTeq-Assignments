package com.example.dto;


import jakarta.validation.constraints.NotBlank;


public class RequestDTO {

    // creating private data members with validation
    @NotBlank(message = "title required")
    private String title;

    @NotBlank(message = "description requied")
    private String description;

    public RequestDTO(String title, String description) {
        this.title = title;
        this.description = description;
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

    // getters
    public String getTitle() {
        return title;
    }

    public String geDescription() {
        return description;
    }

}
