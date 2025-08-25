package com.example.Chat_Bot_Lang4J.config;

import dev.langchain4j.data.embedding.Embedding;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 🏠 Local Embedding Service - No API Quota Limits
 * 
 * Sử dụng local algorithms để tạo embeddings without external API calls.
 * Phù hợp cho development và tránh quota limits.
 */
@Service
@Slf4j
public class LocalEmbeddingService {

    private final Map<String, float[]> embeddingCache = new ConcurrentHashMap<>();

    // Pre-computed embeddings cho intent examples (để tăng tốc)
    private final Map<String, float[]> intentEmbeddingCache = new ConcurrentHashMap<>();

    /**
     * 🎯 Tạo embedding bằng Simple TF-IDF + Word Hash Encoding
     * Phương pháp đơn giản nhưng hiệu quả cho intent classification
     */
    public Embedding embedText(String text) {
        try {
            // Check cache first
            String cacheKey = text.toLowerCase().trim();
            if (embeddingCache.containsKey(cacheKey)) {
                return Embedding.from(embeddingCache.get(cacheKey));
            }

            // Generate embedding using local algorithm
            float[] embedding = generateLocalEmbedding(text);

            // Cache result
            embeddingCache.put(cacheKey, embedding);

            log.debug("✅ Generated local embedding for text: '{}' (dims: {})",
                    text.substring(0, Math.min(50, text.length())), embedding.length);

            return Embedding.from(embedding);

        } catch (Exception e) {
            log.error("❌ Failed to generate local embedding for: {}", text, e);
            // Return default embedding if fails
            return Embedding.from(getDefaultEmbedding());
        }
    }

    /**
     * 🧮 Simple but Effective Local Embedding Algorithm
     * Combines multiple features:
     * - Word hash encoding
     * - Character n-grams
     * - Length features
     * - Vietnamese-specific features
     */
    private float[] generateLocalEmbedding(String text) {
        if (text == null || text.trim().isEmpty()) {
            return getDefaultEmbedding();
        }

        text = text.toLowerCase().trim();
        int embeddingDim = 384; // Standard embedding dimension
        float[] embedding = new float[embeddingDim];

        // 1. Word Hash Features (128 dims)
        String[] words = text.split("\\s+");
        for (String word : words) {
            if (!word.isEmpty()) {
                int hash = Math.abs(word.hashCode()) % 128;
                embedding[hash] += 1.0f / words.length;
            }
        }

        // 2. Character Bi-gram Features (128 dims)
        for (int i = 0; i < text.length() - 1; i++) {
            String bigram = text.substring(i, i + 2);
            int hash = Math.abs(bigram.hashCode()) % 128;
            embedding[128 + hash] += 1.0f / (text.length() - 1);
        }

        // 3. Vietnamese Keywords Detection (64 dims)
        Map<String, Integer> vietnameseKeywords = getVietnameseKeywords();
        for (Map.Entry<String, Integer> entry : vietnameseKeywords.entrySet()) {
            if (text.contains(entry.getKey())) {
                int index = 256 + (entry.getValue() % 64);
                embedding[index] += 1.0f;
            }
        }

        // 4. Intent-specific Features (64 dims)
        addIntentSpecificFeatures(text, embedding, 320);

        // 5. Normalize embedding
        normalizeEmbedding(embedding);

        return embedding;
    }

    /**
     * 🇻🇳 Vietnamese Keywords for better classification
     */
    private Map<String, Integer> getVietnameseKeywords() {
        Map<String, Integer> keywords = new HashMap<>();

        // Database query indicators
        keywords.put("id", 0);
        keywords.put("giá", 1);
        keywords.put("sản phẩm", 2);
        keywords.put("stock", 3);
        keywords.put("tồn kho", 4);
        keywords.put("số lượng", 5);
        keywords.put("thống kê", 6);
        keywords.put("báo cáo", 7);
        keywords.put("danh mục", 8);
        keywords.put("category", 9);
        keywords.put("từ", 10);
        keywords.put("đến", 11);
        keywords.put("triệu", 12);
        keywords.put("dưới", 13);
        keywords.put("trên", 14);

        // Vector search indicators
        keywords.put("gợi ý", 20);
        keywords.put("tư vấn", 21);
        keywords.put("recommend", 22);
        keywords.put("phù hợp", 23);
        keywords.put("nên", 24);
        keywords.put("so sánh", 25);
        keywords.put("đánh giá", 26);
        keywords.put("review", 27);
        keywords.put("chọn", 28);
        keywords.put("lời khuyên", 29);
        keywords.put("tốt nhất", 30);
        keywords.put("đặc điểm", 31);
        keywords.put("tính năng", 32);
        keywords.put("ưu điểm", 33);
        keywords.put("nhược điểm", 34);

        // Web search indicators
        keywords.put("tin tức", 40);
        keywords.put("xu hướng", 41);
        keywords.put("mới nhất", 42);
        keywords.put("thông tin", 43);
        keywords.put("cập nhật", 44);
        keywords.put("hiện tại", 45);
        keywords.put("trend", 46);
        keywords.put("news", 47);
        keywords.put("latest", 48);
        keywords.put("thị trường", 49);
        keywords.put("công nghệ", 50);

        return keywords;
    }

    /**
     * 🎯 Add Intent-specific Features
     */
    private void addIntentSpecificFeatures(String text, float[] embedding, int offset) {
        // Database query features
        if (text.matches(".*\\b(id|giá|stock|số lượng|thống kê|danh mục).*")) {
            embedding[offset] += 1.0f;
        }

        if (text.matches(".*\\b\\d+.*")) { // Contains numbers
            embedding[offset + 1] += 1.0f;
        }

        // Vector search features
        if (text.matches(".*\\b(gợi ý|tư vấn|recommend|phù hợp|nên|chọn).*")) {
            embedding[offset + 10] += 1.0f;
        }

        // Web search features
        if (text.matches(".*\\b(tin tức|xu hướng|mới nhất|thông tin|cập nhật).*")) {
            embedding[offset + 20] += 1.0f;
        }

        // Length features
        if (text.length() > 50) {
            embedding[offset + 30] += 1.0f; // Long queries -> more likely vector search
        }

        if (text.split("\\s+").length <= 3) {
            embedding[offset + 31] += 1.0f; // Short queries -> more likely database
        }
    }

    /**
     * 📏 Normalize embedding vector
     */
    private void normalizeEmbedding(float[] embedding) {
        float norm = 0.0f;
        for (float value : embedding) {
            norm += value * value;
        }
        norm = (float) Math.sqrt(norm);

        if (norm > 0) {
            for (int i = 0; i < embedding.length; i++) {
                embedding[i] /= norm;
            }
        }
    }

    /**
     * 🔧 Default embedding when all else fails
     */
    private float[] getDefaultEmbedding() {
        float[] defaultEmbedding = new float[384];
        Arrays.fill(defaultEmbedding, 0.001f); // Small positive values
        return defaultEmbedding;
    }

    /**
     * 📊 Calculate cosine similarity between two embeddings
     */
    public double calculateSimilarity(Embedding embedding1, Embedding embedding2) {
        float[] vec1 = embedding1.vector();
        float[] vec2 = embedding2.vector();

        if (vec1.length != vec2.length) {
            log.warn("⚠️ Embedding dimensions don't match: {} vs {}", vec1.length, vec2.length);
            return 0.0;
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * 🧹 Clear cache (useful for testing)
     */
    public void clearCache() {
        embeddingCache.clear();
        intentEmbeddingCache.clear();
        log.info("🧹 Embedding cache cleared");
    }

    /**
     * 📈 Get cache statistics
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("embeddingCacheSize", embeddingCache.size());
        stats.put("intentCacheSize", intentEmbeddingCache.size());
        return stats;
    }
}