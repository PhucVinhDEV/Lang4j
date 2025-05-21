package com.example.Chat_Bot_Lang4J.model;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.File;
import java.io.IOException;

@Configuration
@Slf4j
public class Gemini {
    @Value("${spring.google.ai.project-id}")
    private String projectId;

    @Value("${spring.google.ai.location}")
    private String location;

    @Value("${spring.google.ai.model}")
    private String modelName;

    @Value("${spring.google.ai.credentials-path}")
    private String credentialsPath;

    @Value("${spring.google.ai.credentials-file}")
    private String credentialsFileName;

    @PostConstruct
    public void init() {
        log.info("Initializing Gemini configuration");

        String fullCredentialsPath = credentialsPath + credentialsFileName;
        File credentialsFile = new File(fullCredentialsPath);

        if (credentialsFile.exists()) {
            System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", fullCredentialsPath);
            log.info("Credentials file found and environment variable set.");
        } else {
            log.error("Credentials file NOT FOUND at: {}", fullCredentialsPath);
        }
    }

    @Bean
    public ChatModel geminiChatModel() throws IOException {
        String credentials = System.getProperty("GOOGLE_APPLICATION_CREDENTIALS");

        if (credentials == null || !new File(credentials).exists()) {
            throw new IOException("Credentials not properly configured.");
        }

        return VertexAiGeminiChatModel.builder()
                .project(projectId)
                .location(location)
                .modelName(modelName)
                .build();
    }
}
