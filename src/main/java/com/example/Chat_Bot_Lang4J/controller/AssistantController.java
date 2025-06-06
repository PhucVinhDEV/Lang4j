package com.example.Chat_Bot_Lang4J.controller;

import com.example.Chat_Bot_Lang4J.config.Assistant;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AssistantController {
    private final Assistant assistant;

    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        return assistant.chat(message);
    }
}
