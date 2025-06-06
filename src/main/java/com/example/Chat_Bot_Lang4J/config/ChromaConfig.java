package com.example.Chat_Bot_Lang4J.config;

import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.chromadb.ChromaDBContainer;

@Configuration
public class ChromaConfig {


    private ChromaDBContainer chromaContainer;


    @Value("${chroma.collection-name}")
    private String collectionName;


    @Value("${chroma.startup-wait-ms}")
    private int chromaStartupWaitMs;

    @PostConstruct
    public void startContainer() {
        chromaContainer = new ChromaDBContainer("chromadb/chroma:0.5.4");
        chromaContainer.start();
        // Optionally wait here or rely on bean method to wait
    }

    @PreDestroy
    public void stopContainer() {
        if (chromaContainer != null) {
            chromaContainer.stop();
        }
    }

    @Bean
    public ChromaEmbeddingStore chromaEmbeddingStore() {
        String baseUrl = chromaContainer.getEndpoint();
        try {
            Thread.sleep(chromaStartupWaitMs); // wait for startup
            return ChromaEmbeddingStore.builder()
                    .baseUrl(baseUrl)
                    .collectionName(collectionName)
                    .logRequests(true)
                    .logResponses(true)
                    .build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted waiting for ChromaDB", e);
        }
    }
}
