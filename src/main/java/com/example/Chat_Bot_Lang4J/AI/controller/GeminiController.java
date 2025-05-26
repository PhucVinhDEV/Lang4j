package com.example.Chat_Bot_Lang4J.AI.controller;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    @Autowired
    private ChatModel geminiChatModel;

    // API endpoint để test text prompt
    @PostMapping("/text")
    public ResponseEntity<Map<String, String>> processTextPrompt(@RequestBody Map<String, String> request) {
        try {
            String prompt = request.get("prompt");
            if (prompt == null || prompt.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Prompt cannot be empty"));
            }

            var response = geminiChatModel.chat(UserMessage.from(prompt));
            Map<String, String> result = new HashMap<>();
            result.put("response", response.aiMessage().text());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // API endpoint để test image và text prompt
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> processImagePrompt(
            @RequestParam("image") MultipartFile image,
            @RequestParam("prompt") String prompt) {

        try {
            if (image.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Image cannot be empty"));
            }

            if (prompt == null || prompt.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Prompt cannot be empty"));
            }

            byte[] imageBytes = image.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            var response = geminiChatModel.chat(
                    UserMessage.from(
                            ImageContent.from(base64Image),
                            TextContent.from(prompt)
                    )
            );

            Map<String, String> result = new HashMap<>();
            result.put("response", response.aiMessage().text());

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to process image: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // API endpoint để test với image URL và text prompt
    @PostMapping("/image-url")
    public ResponseEntity<Map<String, String>> processImageUrlPrompt(@RequestBody Map<String, String> request) {
        try {
            String imageUrl = request.get("imageUrl");
            String prompt = request.get("prompt");

            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Image URL cannot be empty"));
            }

            if (prompt == null || prompt.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Prompt cannot be empty"));
            }

            var response = geminiChatModel.chat(
                    UserMessage.from(
                            ImageContent.from(imageUrl),
                            TextContent.from(prompt)
                    )
            );

            Map<String, String> result = new HashMap<>();
            result.put("response", response.aiMessage().text());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}