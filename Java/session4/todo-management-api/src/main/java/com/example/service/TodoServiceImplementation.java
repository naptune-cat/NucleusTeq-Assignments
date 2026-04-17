package com.example.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.RequestDTO;
import com.example.dto.ResponseDTO;

import com.example.entity.Todo;
import com.example.repository.TodoRepository;

@Service
public class TodoServiceImplementation implements TodoService {
    private final TodoRepository todoRepository;

    public TodoServiceImplementation(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public ResponseDTO createTodo(RequestDTO requestDTO) {

        // requestDTO -> Entity mapping
        Todo todo = new Todo();
        todo.setTitle(requestDTO.getTitle());
        todo.setDescription(requestDTO.getDescription());
        todo.setStatus(requestDTO.getStatus());

        //saving the todo in our db
        Todo savedTodo = todoRepository.save(todo);

        // entity -> responseDTO mapping
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setId(savedTodo.getId());
        responseDTO.setTitle(savedTodo.getTitle());
        responseDTO.setDescription(savedTodo.getDescription());
        responseDTO.setStatus(savedTodo.getStatus());
        responseDTO.setCreatedAt(savedTodo.getCreatedAt());

        return responseDTO;
    }
    
   
    

}
