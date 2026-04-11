package com.example.usermanagementsystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.usermanagementsystem.service.MessageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;
    //making reference of message service

    //constructor injection
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
    
    //when we hit the "/url" in the @RequestMapping annotation the method with @PostMapping annotation will run
    @PostMapping 
    public String getMessage(@RequestParam String type) {
        return messageService.getMessage(type);
    }
    
}
