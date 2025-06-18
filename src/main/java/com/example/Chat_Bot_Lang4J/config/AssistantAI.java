package com.example.Chat_Bot_Lang4J.config;

import com.example.Chat_Bot_Lang4J.Data.Service.DatabaseService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AssistantAI {

        private final DatabaseService databaseService;

        @Bean
        public Assistant assistant(ChromaEmbeddingStore chromaEmbeddingStore,
                        EmbeddingModel embeddingModel,
                        ChatModel chatModel) {

                ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                                .embeddingStore(chromaEmbeddingStore)
                                .embeddingModel(embeddingModel)
                                .maxResults(5) // Lấy tối đa 5 kết quả từ vector store
                                .minScore(0.7) // Chỉ lấy kết quả có độ tương đồng >= 0.7
                                .build();

                // Prompt template cải thiện để tích hợp dữ liệu từ database
                PromptTemplate promptTemplate = PromptTemplate
                                .from("""
                                                Bạn là một trợ lý AI thông minh cho hệ thống quản lý sản phẩm.

                                                THÔNG TIN TỪ CƠ SỞ DỮ LIỆU:
                                                {{database_info}}

                                                THÔNG TIN TỪ VECTOR STORE (nếu có):
                                                {{documents}}

                                                CÂU HỎI CỦA NGƯỜI DÙNG:
                                                {{user_message}}

                                                HƯỚNG DẪN TRẢ LỜI:
                                                1. Nếu câu hỏi liên quan đến thông tin cụ thể (giá, số lượng, ID, tên sản phẩm), hãy sử dụng dữ liệu từ cơ sở dữ liệu
                                                2. Nếu câu hỏi mang tính ngữ nghĩa hoặc yêu cầu gợi ý, hãy sử dụng thông tin từ vector store
                                                3. Luôn trả lời bằng tiếng Việt một cách tự nhiên và hữu ích
                                                4. Nếu không có thông tin, hãy nói rõ ràng rằng bạn không có thông tin đó
                                                5. Định dạng câu trả lời rõ ràng, có thể sử dụng emoji để dễ đọc

                                                Hãy trả lời câu hỏi của người dùng dựa trên thông tin có sẵn:
                                                """);

                ContentInjector contentInjector = DefaultContentInjector.builder()
                                .promptTemplate(promptTemplate)
                                .metadataKeysToInclude(List.of("file_name", "index", "product_id", "category_id"))
                                .build();

                RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                                .contentRetriever(contentRetriever)
                                .contentInjector(contentInjector)
                                .build();

                return AiServices.builder(Assistant.class)
                                .chatModel(chatModel)
                                .retrievalAugmentor(retrievalAugmentor)
                                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                                .build();
        }
}
