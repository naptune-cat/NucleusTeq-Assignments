package com.example.service;

import java.util.List;

import com.example.entity.Todo;


//making interface for our service for better and cleane code
public interface TodoService {
    Todo createTodo(Todo todo);

    List<Todo> getAllTodo();

    Todo getTodoById(Long id);

    void deleteTodo(Long id);

    Todo updateTodo(Long id, Todo todo);
}
