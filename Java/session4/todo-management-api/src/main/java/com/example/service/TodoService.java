package com.example.service;

import java.util.List;

import com.example.dto.RequestDTO;
import com.example.dto.ResponseDTO;



//making interface for our service for better and cleane code
public interface TodoService {
    ResponseDTO createTodo(RequestDTO requestDTO);

    List<ResponseDTO> getAllTodo();

    ResponseDTO getTodoById(Long id);

    void deleteTodo(Long id);

    ResponseDTO updateTodo(Long id, RequestDTO requestDTO);
}
