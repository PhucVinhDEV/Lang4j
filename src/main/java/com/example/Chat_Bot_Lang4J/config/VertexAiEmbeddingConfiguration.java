package com.example.Chat_Bot_Lang4J.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import dev.langchain4j.model.vertexai.VertexAiEmbeddingModel;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.chromadb.ChromaDBContainer;

import java.util.UUID;

import static dev.langchain4j.internal.Utils.randomUUID;
/**
 * Configuration class for VertexAI Embedding Model and ChromaDB integration.
 *
 * <p>This configuration provides:
 * <ul>
 *   <li>VertexAI embedding model with multilingual support</li>
 *   <li>ChromaDB vector store with flexible deployment options</li>
 *   <li>Automatic container management for development</li>
 *   <li>Production-ready external database support</li>
 * </ul>
 *
 * @author Bitznomad
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class VertexAiEmbeddingConfiguration {

    // VertexAI Configuration Properties - FIXED to match YAML
    @Value("${vertex.ai.project-id}")
    private String vertexProjectId;

    @Value("${vertex.ai.location}")
    private String vertexLocation;

    @Value("${vertex.ai.endpoint}")
    private String vertexEndpoint;

    @Value("${vertex.ai.publisher}")
    private String vertexPublisher;

    @Value("${vertex.ai.model-name}")
    private String vertexModelName;

    @Value("${vertex.ai.max-retries}")
    private int maxRetries;

    @Value("${vertex.ai.max-segments-per-batch}")
    private int maxSegmentsPerBatch;

    @Value("${vertex.ai.max-tokens-per-batch}")
    private int maxTokensPerBatch;

    @Value("${vertex.ai.output-dimensionality}")
    private int outputDimensionality;

    @Value("${vertex.ai.auto-truncate}")
    private boolean autoTruncate;

    @Value("${vertex.ai.task-type}")
    private String taskType;



    /**
     * Creates and configures the VertexAI Embedding Model.
     *
     * @return configured EmbeddingModel instance
     * @throws IllegalStateException if VertexAI configuration is invalid
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        log.info("Initializing VertexAI Embedding Model...");

        validateVertexAiConfiguration();

        try {
            EmbeddingModel model = VertexAiEmbeddingModel.builder()
                    .project(vertexProjectId)
                    .location(vertexLocation)
                    .endpoint(vertexEndpoint)
                    .publisher(vertexPublisher)
                    .modelName(vertexModelName)
                    .maxRetries(maxRetries)
                    .maxSegmentsPerBatch(maxSegmentsPerBatch)
                    .maxTokensPerBatch(maxTokensPerBatch)
                    .taskType(parseTaskType(taskType))
                    .autoTruncate(autoTruncate)
                    .outputDimensionality(outputDimensionality)
                    .build();

            log.info("✅ VertexAI Embedding Model initialized successfully");
            log.info("   Project: {}, Location: {}, Model: {}", vertexProjectId, vertexLocation, vertexModelName);

            return model;
        } catch (Exception e) {
            log.error("❌ Failed to initialize VertexAI Embedding Model", e);
            throw new IllegalStateException("Cannot initialize VertexAI Embedding Model", e);
        }
    }





    // Private Helper Methods

    /**
     * Validates VertexAI configuration parameters.
     *
     * @throws IllegalArgumentException if configuration is invalid
     */
    private void validateVertexAiConfiguration() {
        if (vertexProjectId == null || vertexProjectId.trim().isEmpty()) {
            throw new IllegalArgumentException("VertexAI project ID cannot be null or empty");
        }

        if (vertexLocation == null || vertexLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("VertexAI location cannot be null or empty");
        }

        if (vertexModelName == null || vertexModelName.trim().isEmpty()) {
            throw new IllegalArgumentException("VertexAI model name cannot be null or empty");
        }

        if (maxRetries < 0) {
            throw new IllegalArgumentException("VertexAI max retries cannot be negative");
        }

        if (outputDimensionality <= 0) {
            throw new IllegalArgumentException("VertexAI output dimensionality must be positive");
        }

        if (maxSegmentsPerBatch <= 0) {
            throw new IllegalArgumentException("VertexAI max segments per batch must be positive");
        }

        if (maxTokensPerBatch <= 0) {
            throw new IllegalArgumentException("VertexAI max tokens per batch must be positive");
        }
    }

    /**
     * Parses the task type string to enum.
     *
     * @param taskTypeStr the task type string
     * @return TaskType enum
     */
    private VertexAiEmbeddingModel.TaskType parseTaskType(String taskTypeStr) {
        try {
            return VertexAiEmbeddingModel.TaskType.valueOf(taskTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid task type: {}, using default SEMANTIC_SIMILARITY", taskTypeStr);
            return VertexAiEmbeddingModel.TaskType.SEMANTIC_SIMILARITY;
        }
    }






}
