package com.example.dto;

import java.time.LocalDateTime;

import com.example.enums.TodoStatus;

public class ResponseDTO {
    
    private Long id;
    private String title;
    private String description;
    private TodoStatus status;
    private LocalDateTime createdAt;

    public ResponseDTO(Long id, String title, String description, TodoStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }
    
    //empty constructor
    public ResponseDTO() {
    }
    
    //getters

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TodoStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    //setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TodoStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
