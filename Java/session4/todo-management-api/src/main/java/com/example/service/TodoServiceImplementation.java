package com.example.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.dto.RequestDTO;
import com.example.dto.ResponseDTO;

import com.example.entity.Todo;
import com.example.enums.TodoStatus;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.TodoMapper;
import com.example.repository.TodoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class TodoServiceImplementation implements TodoService {
    private final TodoRepository todoRepository;
    private final static Logger logger = LoggerFactory.getLogger(TodoServiceImplementation.class);


    public TodoServiceImplementation(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public ResponseDTO createTodo(RequestDTO requestDTO) {

        logger.info("Started Creating new todo in service layer");
        // requestDTO -> Entity mapping
        Todo todo = TodoMapper.dtoToEntityMapping(requestDTO);

        
        todo.setCreatedAt(LocalDateTime.now());
        
        //checking if status is null

        if (requestDTO.getStatus() == null) {
            todo.setStatus(TodoStatus.PENDING);
        }
        //saving the todo in our db
        Todo savedTodo = todoRepository.save(todo);

        // entity -> responseDTO mapping
        ResponseDTO responseDTO = TodoMapper.entityToDToMapping(savedTodo);
        logger.info("Todo created successfully");
        return responseDTO;
    }
    
    
    @Override
    public List<ResponseDTO> getAllTodo() {
        logger.info("getting all todos");
        List<Todo> todos = todoRepository.findAll();

        return todos.stream().map(todo -> {
            ResponseDTO responseDTO = TodoMapper.entityToDToMapping(todo);
            return responseDTO;
        }).collect(Collectors.toList());
    }
    

    @Override
    public ResponseDTO getTodoById(Long id) {

        // optional is a container which allows us to handle uncertain data
        // here todo for this id may or may not exist
        logger.info("getting todo by id: {}",id);
        Optional<Todo> todoOptional = todoRepository.findById(id);

        if (todoOptional.isEmpty()) {
            throw new ResourceNotFoundException("Id: " + id + " does not exist in DB");
        }

        // converting Optional<Todo> to Todo type
        Todo todo = todoOptional.get();

        // entity -> DTO mapping
        ResponseDTO responseDTO = TodoMapper.entityToDToMapping(todo);
        logger.info("Found todo with id: {}",id);
        return responseDTO;
    }

    @Override
    public void deleteTodo(Long id) {
        Optional<Todo> todoOptional = todoRepository.findById(id);

        logger.info("Deleting todo with id: {}",id);
        if (todoOptional.isEmpty()) {
            throw new ResourceNotFoundException("No such id exists");
        }

        //converting back to Todo type
        Todo todo = todoOptional.get();

        // calling delete method
        todoRepository.delete(todo);
        logger.info("Deleted todo with id: {}",id);
    }

   @Override
    public ResponseDTO updateTodo(Long id, RequestDTO requestDTO) {
        logger.info("Updating todo with id: {}", id);

        Optional<Todo> existingTodo = todoRepository.findById(id);

        if (existingTodo.isEmpty()) {
            throw new ResourceNotFoundException("No such Id exists");
        }

        Todo todo = existingTodo.get();

        // entity -> request dto mapping 
        // here we are not calling the dtoToEntityMapping() because it can lose the id and createdAt fields and db might thing this is a new Todo and hence might make a new Todo with new id

        todo.setTitle(requestDTO.getTitle());
        todo.setDescription(requestDTO.getDescription());

        if (requestDTO.getStatus() == null) {
        todo.setStatus(TodoStatus.PENDING);
        } else {
            todo.setStatus(requestDTO.getStatus());
        }

        Todo updatedTodo = todoRepository.save(todo);

        ResponseDTO responseDTO = TodoMapper.entityToDToMapping(updatedTodo);

        logger.info("Updated todo successfully with id: {}", id);

        return responseDTO;
    }
}
