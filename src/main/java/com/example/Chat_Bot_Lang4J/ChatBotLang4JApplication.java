package com.example.Chat_Bot_Lang4J;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ChatBotLang4JApplication {

	public static void main(String[] args) throws IOException {

		SpringApplication.run(ChatBotLang4JApplication.class, args);
		GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();

	}
}
