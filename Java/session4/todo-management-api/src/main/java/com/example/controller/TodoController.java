package com.example.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.RequestDTO;
import com.example.dto.ResponseDTO;
import com.example.service.TodoServiceImplementation;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// annotations
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

// logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/todos")
public class TodoController {
    private final TodoServiceImplementation todoService;

    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

    public TodoController(TodoServiceImplementation todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createTodo(@Valid @RequestBody RequestDTO requestDTO) {
        logger.info("Creating new Todo");
        ResponseDTO createdTodo = todoService.createTodo(requestDTO);

        return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
    }


    @GetMapping 
    public ResponseEntity<List<ResponseDTO>> getAllTodo() {
        logger.info("Retrieving all todos");
        List<ResponseDTO> todos = todoService.getAllTodo();
        return ResponseEntity.ok(todos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getTodoById(@PathVariable Long id) {
        logger.info("Retrieving todo with id: {}",id);
        ResponseDTO todo = todoService.getTodoById(id);
        return ResponseEntity.ok(todo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateTodo(@PathVariable Long id, @RequestBody RequestDTO requestDTO) {
        logger.info("Updating todo with id: {}",id);
        ResponseDTO todo = todoService.updateTodo(id, requestDTO);
        return ResponseEntity.ok(todo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTodo(@PathVariable Long id) {
        logger.info("Deleting todo with id: {}",id);
        todoService.deleteTodo(id);
        return ResponseEntity.ok("Todo Deleted Successfully");
    }
}

