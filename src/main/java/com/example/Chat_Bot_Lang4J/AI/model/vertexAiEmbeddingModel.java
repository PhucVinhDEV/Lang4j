package com.example.Chat_Bot_Lang4J.AI.model;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.springframework.context.annotation.Bean;
import dev.langchain4j.model.vertexai.VertexAiEmbeddingModel;

public class vertexAiEmbeddingModel {
    private static final String PROJECT_ID = "bitznomad";
    private static final String MODEL_NAME = "text-multilingual-embedding-002";

    @Bean
    public EmbeddingModel embeddingModel() {
        return VertexAiEmbeddingModel.builder()
                .project(PROJECT_ID)
                .location("us-central1")
                .endpoint("us-central1-aiplatform.googleapis.com:443")
                .publisher("google")
                .modelName(MODEL_NAME)
                .maxRetries(2)             // 2 by default
                .maxSegmentsPerBatch(40)  // up to 250 segments per batch
                .maxTokensPerBatch(2048)   // up to 2048 tokens per segment
                .taskType(VertexAiEmbeddingModel.TaskType.CLASSIFICATION)                // see below for the different task types
                // for the text segment to identify its document origin
                .autoTruncate(false)       // false by default: truncates segments longer than 2,048 input tokens
                .outputDimensionality(512) // for models that support different output vector dimensions
                .build();
    }

}
