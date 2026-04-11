package com.example.usermanagementsystem.service;

import org.springframework.stereotype.Service;

import com.example.usermanagementsystem.component.LongMessageFormatter;
import com.example.usermanagementsystem.component.ShortMessageFormatter;

@Service
public class MessageService {
    private final LongMessageFormatter longMessageFormatter;
    private final ShortMessageFormatter shortMessageFormatter;
   
    public MessageService(LongMessageFormatter longMessageFormatter, ShortMessageFormatter shortMessageFormatter) {
        this.longMessageFormatter = longMessageFormatter;
        this.shortMessageFormatter = shortMessageFormatter;
    }

    public String getMessage(String type) {
        if (type.equalsIgnoreCase("SHORT")) {
            return shortMessageFormatter.messageFormat();
        }
        return longMessageFormatter.messageFormat();
    }
}
