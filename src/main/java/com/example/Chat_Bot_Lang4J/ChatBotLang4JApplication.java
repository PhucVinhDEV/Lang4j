package com.example.Chat_Bot_Lang4J;

import com.example.Chat_Bot_Lang4J.model.Gemini;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ChatBotLang4JApplication {

	public static void main(String[] args) {

		SpringApplication.run(ChatBotLang4JApplication.class, args);


	}
}
