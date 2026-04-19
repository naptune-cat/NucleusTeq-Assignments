package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.client.NotificationServiceClient;
import com.example.dto.RequestDTO;
import com.example.dto.ResponseDTO;
import com.example.entity.Todo;
import com.example.enums.TodoStatus;
import com.example.repository.TodoRepository;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplementationTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private NotificationServiceClient notificationServiceClient;

    @InjectMocks
    private TodoServiceImplementation todoService;

    @Test
    void shouldCreateTodoSuccessfully() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setTitle("Test Todo");
        requestDTO.setDescription("Test Description");

        Todo savedTodo = new Todo();
        savedTodo.setId(1L);
        savedTodo.setTitle("Test Todo");
        savedTodo.setStatus(TodoStatus.PENDING);
        savedTodo.setDescription("Test Description");

        when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);

        ResponseDTO response = todoService.createTodo(requestDTO);

        assertEquals("Test Todo", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertEquals(TodoStatus.PENDING, response.getStatus());

        verify(todoRepository, times(1)).save(any(Todo.class));
        verify(notificationServiceClient, times(1))
                .sendNotification("Notification sent for new Todo");
    }

    @Test
    void shouldGetByIdSuccessfully() {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Test title");
        todo.setStatus(TodoStatus.PENDING);
        todo.setDescription("Test description");

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        ResponseDTO response = todoService.getTodoById(1L);

        assertEquals(1L, response.getId());
        assertEquals("Test title", response.getTitle());
        assertEquals("Test description", response.getDescription());
        assertEquals(TodoStatus.PENDING, response.getStatus());
    }
    
    @Test
    void shouldGetAllTodo() {
        //making 2 entries in our fake db
        Todo todo1 = new Todo();
        todo1.setId(1L);
        todo1.setTitle("Test Todo 1");
        todo1.setDescription("Test description 1");
        todo1.setStatus(TodoStatus.PENDING);

        Todo todo2 = new Todo();
        todo2.setId(2L);
        todo2.setTitle("Test Todo 2");
        todo2.setDescription("Test description 2");
        todo2.setStatus(TodoStatus.COMPLETED);

        //inserting in list
        List<Todo> todoList = Arrays.asList(todo1, todo2);

        when(todoRepository.findAll()).thenReturn(todoList);

        // calling response and saving it in a List
        List<ResponseDTO> response = todoService.getAllTodo();

        //checking the response against DB

        // checking if size of response list is same as the tests todo added in fake db
        assertEquals(2, response.size());

        assertEquals("Test Todo 1", response.get(0).getTitle());
        assertEquals("Test description 1", response.get(0).getDescription());
        assertEquals(TodoStatus.PENDING, response.get(0).getStatus());

        assertEquals("Test Todo 2", response.get(1).getTitle());
        assertEquals("Test description 2", response.get(1).getDescription());
        assertEquals(TodoStatus.COMPLETED, response.get(1).getStatus());

        verify(todoRepository, times(1)).findAll();

    }

    @Test
    void shouldDeleteTodoSuccessfully() {
        Todo todo = new Todo();
        todo.setId(1L);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        todoService.deleteTodo(1L);

        verify(todoRepository, times(1)).delete(todo);
    }


    @Test
void shouldUpdateTodoSuccessfully() {
    RequestDTO requestDTO = new RequestDTO();
    requestDTO.setTitle("Updated Todo");
    requestDTO.setDescription("Updated Description");
    requestDTO.setStatus(TodoStatus.COMPLETED);

    Todo existingTodo = new Todo();
    existingTodo.setId(1L);
    existingTodo.setTitle("Old Todo");
    existingTodo.setDescription("Old Description");
    existingTodo.setStatus(TodoStatus.PENDING);

    Todo updatedTodo = new Todo();
    updatedTodo.setId(1L);
    updatedTodo.setTitle("Updated Todo");
    updatedTodo.setDescription("Updated Description");
    updatedTodo.setStatus(TodoStatus.COMPLETED);

    when(todoRepository.findById(1L)).thenReturn(Optional.of(existingTodo));
    when(todoRepository.save(any(Todo.class))).thenReturn(updatedTodo);

    ResponseDTO response = todoService.updateTodo(1L, requestDTO);

    assertEquals("Updated Todo", response.getTitle());
    assertEquals("Updated Description", response.getDescription());
    assertEquals(TodoStatus.COMPLETED, response.getStatus());

    verify(todoRepository, times(1)).findById(1L);
    verify(todoRepository, times(1)).save(any(Todo.class));
}
}