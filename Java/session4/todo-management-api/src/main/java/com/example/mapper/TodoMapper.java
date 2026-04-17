package com.example.mapper;

import com.example.dto.RequestDTO;
import com.example.dto.ResponseDTO;
import com.example.entity.Todo;

public class TodoMapper {

    // for DTO -> Entity mapping

    public static Todo dtoToEntityMapping(RequestDTO requestDTO) {
        Todo todo = new Todo();

        todo.setTitle(requestDTO.getTitle());
        todo.setDescription(requestDTO.getDescription());
        todo.setStatus(requestDTO.getStatus());

        return todo;
    }

    // for entity->DTO mapping

    public static ResponseDTO entityToDToMapping(Todo todo) {
        ResponseDTO responseDto = new ResponseDTO();

        responseDto.setId(todo.getId());
        responseDto.setTitle(todo.getTitle());
        responseDto.setDescription(todo.getDescription());
        responseDto.setStatus(todo.getStatus());
        responseDto.setCreatedAt(todo.getCreatedAt());

        return responseDto;
    }
}