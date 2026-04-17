package com.example.service;

import java.util.List;
import java.util.Optional;
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
    
    
    @Override
    public List<ResponseDTO> getAllTodo() {

        List<Todo> todos = todoRepository.findAll();

        return todos.stream().map(todo -> {
            ResponseDTO dto = new ResponseDTO();
            dto.setId(todo.getId());
            dto.setTitle(todo.getTitle());
            dto.setDescription(todo.getDescription());
            dto.setStatus(todo.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }
    

    @Override
    public ResponseDTO getTodoById(Long id) {

        // optional is a container which allows us to handle uncertain data
        // here todo may or may not exist
        Optional<Todo> todoOptional = todoRepository.findById(id);

        if (todoOptional == null) {
            throw new RuntimeException("Id: "+id+" does not exist in DB");
        }

        // converting Optional<Todo> to Todo type
        Todo todo = todoOptional.get();

        // entity ->DTO mapping

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setId(todo.getId());
        responseDTO.setTitle(todo.getTitle());
        responseDTO.setDescription(todo.getDescription());
        responseDTO.setStatus(todo.getStatus());
        responseDTO.setCreatedAt(todo.getCreatedAt());

        return responseDTO;
    }

}
