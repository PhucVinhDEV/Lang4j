package com.example.Chat_Bot_Lang4J.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SemanticIntentClassifier {

    private final EmbeddingModel embeddingModel;
    private final LocalEmbeddingService localEmbeddingService;

    @Value("${embedding.use-local:false}")
    private boolean useLocalEmbedding;

    @Value("${embedding.fallback-enabled:true}")
    private boolean fallbackEnabled;

    public SemanticIntentClassifier(EmbeddingModel embeddingModel, LocalEmbeddingService localEmbeddingService) {
        this.embeddingModel = embeddingModel;
        this.localEmbeddingService = localEmbeddingService;
    }

    // 🎯 3 Intent Chính - Optimized for Chatbot Routing
    private static final Map<String, List<String>> INTENT_EXAMPLES = Map.of(

            "DATABASE_QUERY", Arrays.asList(
                    // 🗄️ FUNCTION CALLING - Structured Data Queries
                    // ID và thông tin cụ thể
                    "Sản phẩm ID 5 giá bao nhiêu",
                    "Cho tôi biết giá của iPhone",
                    "Hiển thị chi tiết product ID 10",
                    "Giá MacBook là bao nhiêu",
                    "Product 15 có còn hàng không",
                    "Thông tin sản phẩm mã số 20",
                    "Xem chi tiết sản phẩm ID 100",

                    // Tồn kho và số lượng
                    "Tồn kho còn bao nhiêu sản phẩm",
                    "Stock quantity của Samsung Galaxy",
                    "Số lượng hàng tồn kho hiện tại",
                    "Kiểm tra tồn kho sản phẩm",
                    "Còn bao nhiêu cái trong kho",
                    "Sản phẩm nào sắp hết hàng",
                    "Stock dưới 10 sản phẩm",

                    // Thống kê và báo cáo
                    "Thống kê số lượng danh mục",
                    "Có bao nhiêu sản phẩm điện tử",
                    "Tổng số sản phẩm trong hệ thống",
                    "Báo cáo tình hình kinh doanh",
                    "Thống kê doanh số",
                    "Danh sách tất cả categories",
                    "Số lượng sản phẩm theo danh mục",

                    // Khoảng giá và range queries
                    "Sản phẩm từ 10 triệu đến 20 triệu",
                    "Tìm sản phẩm dưới 5 triệu",
                    "Laptop trong khoảng 15-25 triệu",
                    "Điện thoại giá từ 8-12 triệu",
                    "Sản phẩm trên 30 triệu",
                    "Tìm theo khoảng giá cụ thể",

                    // Danh mục cụ thể
                    "Danh mục số 2 có gì",
                    "Category 1 gồm những sản phẩm nào",
                    "Sản phẩm thuộc nhóm điện tử",
                    "Danh sách sản phẩm category electronics",
                    "Xem tất cả sản phẩm trong danh mục"),

            "VECTOR_SEARCH", Arrays.asList(
                    // 📄 SEMANTIC SEARCH - Contextual & Advisory Queries
                    // Gợi ý và tư vấn
                    "Gợi ý sản phẩm phù hợp cho tôi",
                    "Tư vấn laptop cho sinh viên",
                    "Recommend điện thoại tốt nhất",
                    "Sản phẩm nào phù hợp với nhu cầu của tôi",
                    "Hãy giúp tôi chọn laptop gaming",
                    "Bạn có thể tư vấn cho tôi không",
                    "Cho tôi lời khuyên về sản phẩm",
                    "Nên mua gì cho người già",
                    "Sản phẩm tốt nhất trong tầm giá",
                    "Recommendation cho dân văn phòng",

                    // So sánh và đánh giá
                    "So sánh các sản phẩm tương tự",
                    "Đâu là sự khác biệt giữa iPhone và Samsung",
                    "Ưu nhược điểm của sản phẩm này",
                    "Review và đánh giá chi tiết",
                    "Pros and cons của laptop này",
                    "Phân tích điểm mạnh điểm yếu",
                    "Đánh giá tổng quan về sản phẩm",

                    // Mô tả và features
                    "Mô tả đặc điểm của sản phẩm này",
                    "Features chính của laptop này",
                    "Tính năng nổi bật là gì",
                    "Điểm mạnh của sản phẩm",
                    "Specification và thông số kỹ thuật",
                    "Đặc điểm nổi bật",
                    "Tính năng đặc biệt",

                    // Use cases và scenarios
                    "Laptop nào phù hợp cho designer",
                    "Điện thoại chụp ảnh đẹp",
                    "Sản phẩm cho người mới bắt đầu",
                    "Thiết bị phù hợp cho công việc",
                    "Sản phẩm cho học sinh sinh viên",

                    // Document và knowledge search
                    "Tìm kiếm trong documents",
                    "Có file PDF nào về chủ đề này không",
                    "Tài liệu hướng dẫn sử dụng",
                    "Manual và catalog sản phẩm",
                    "Thông tin từ kho tài liệu"),

            "HYBRID", Arrays.asList(
                    // 🔄 GENERAL LLM - Conversational & General Queries
                    // Chào hỏi và conversation
                    "Xin chào",
                    "Hello",
                    "Bạn khỏe không",
                    "Cảm ơn bạn",
                    "Tạm biệt",
                    "Bạn là ai",
                    "Bạn có thể làm gì",

                    // Câu hỏi chung chung
                    "Giải thích cho tôi về AI",
                    "Tôi muốn biết về công nghệ",
                    "Hôm nay thế nào",
                    "Bạn nghĩ sao về điều này",
                    "Có ý kiến gì không",

                    // Yêu cầu chung
                    "Giúp tôi hiểu về hệ thống này",
                    "Làm thế nào để sử dụng",
                    "Hướng dẫn tôi",
                    "Tôi cần trợ giúp",
                    "Có thể hỗ trợ tôi không",

                    // Câu hỏi mơ hồ
                    "Còn gì khác không",
                    "Tôi muốn tìm hiểu thêm",
                    "Có gì thú vị",
                    "Nói cho tôi biết",
                    "Tôi quan tâm đến",

                    // Educational và general knowledge
                    "Blockchain là gì",
                    "Machine Learning hoạt động như thế nào",
                    "Xu hướng công nghệ hiện tại",
                    "Lịch sử phát triển AI",
                    "Tương lai của công nghệ"));

    // Cache embeddings để tối ưu performance - Thread-safe
    private final Map<String, List<Embedding>> intentEmbeddingsCache = new ConcurrentHashMap<>();

    // Cache user embeddings (với TTL - có thể implement sau)
    private final Map<String, Embedding> userEmbeddingCache = new ConcurrentHashMap<>();

    // 📊 Tuned thresholds cho 3 intent system
    private static final double BASE_CONFIDENCE_THRESHOLD = 0.70; // Lowered for 3-way classification
    private static final double HIGH_CONFIDENCE_THRESHOLD = 0.85;
    private static final double MIN_CONFIDENCE_THRESHOLD = 0.55; // More lenient

    /**
     * 🧠 Enhanced intent classification cho 3 intent system
     */
    public IntentClassificationResult classifyIntent(String userMessage) {
        // Validation
        if (userMessage == null || userMessage.trim().isEmpty()) {
            log.warn("⚠️ Empty or null user message provided");
            return new IntentClassificationResult("HYBRID", 0.0, false, Map.of(), "Empty message");
        }

        if (userMessage.length() > 500) {
            log.warn("⚠️ User message too long: {} characters", userMessage.length());
            userMessage = userMessage.substring(0, 500); // Truncate
        }

        try {
            log.info("🧠 Classifying intent for: {}", userMessage);

            // Check cache first
            Embedding userEmbedding = getUserEmbedding(userMessage);

            String bestIntent = "HYBRID";
            double bestScore = 0.0;
            Map<String, Double> allScores = new HashMap<>();

            // So sánh với từng intent
            for (String intent : INTENT_EXAMPLES.keySet()) {
                double avgSimilarity = calculateIntentSimilarity(userEmbedding, intent);
                allScores.put(intent, avgSimilarity);

                if (avgSimilarity > bestScore) {
                    bestScore = avgSimilarity;
                    bestIntent = intent;
                }
            }

            // 🎯 Enhanced dynamic threshold cho 3-way classification
            double dynamicThreshold = calculateDynamicThreshold(allScores, bestScore);
            boolean isConfident = bestScore >= dynamicThreshold;

            // Special boost for very clear DATABASE patterns (numbers, IDs, "giá", "số
            // lượng")
            if (bestIntent.equals("DATABASE_QUERY") && containsStructuredKeywords(userMessage)) {
                isConfident = true;
                log.info("🎯 DATABASE_QUERY confidence boost due to structured keywords");
            }

            // Additional confidence boost for clear patterns
            if (bestScore > HIGH_CONFIDENCE_THRESHOLD) {
                isConfident = true;
            }

            log.info("🎯 Intent: {} | Score: {:.3f} | Threshold: {:.3f} | Confident: {}",
                    bestIntent, bestScore, dynamicThreshold, isConfident);

            return new IntentClassificationResult(
                    bestIntent,
                    bestScore,
                    isConfident,
                    allScores,
                    String.format("Threshold: %.3f, Keywords: %s", dynamicThreshold,
                            containsStructuredKeywords(userMessage) ? "Yes" : "No"));

        } catch (Exception e) {
            log.error("❌ Error in intent classification", e);
            return new IntentClassificationResult("HYBRID", 0.0, false, Map.of(),
                    "Error: " + e.getMessage());
        }
    }

    /**
     * 🎯 Detect structured keywords cho DATABASE_QUERY
     */
    private boolean containsStructuredKeywords(String message) {
        String lowerMessage = message.toLowerCase();

        // Keywords cho DATABASE queries
        String[] dbKeywords = {
                "id", "giá", "price", "số lượng", "quantity", "stock", "tồn kho",
                "thống kê", "statistics", "báo cáo", "report", "category", "danh mục",
                "từ", "đến", "dưới", "trên", "khoảng", "range", "triệu", "nghìn"
        };

        // Number patterns
        boolean hasNumbers = lowerMessage.matches(".*\\d+.*");

        // Keyword match
        boolean hasDbKeywords = Arrays.stream(dbKeywords)
                .anyMatch(lowerMessage::contains);

        return hasNumbers || hasDbKeywords;
    }

    /**
     * 🔄 Get or create user embedding with caching and fallback
     */
    private Embedding getUserEmbedding(String userMessage) {
        // Simple cache key (có thể improve bằng hash)
        String cacheKey = userMessage.toLowerCase().trim();

        return userEmbeddingCache.computeIfAbsent(cacheKey, key -> {
            try {
                return createEmbeddingWithFallback(userMessage);
            } catch (Exception e) {
                log.error("❌ Failed to create user embedding with fallback for: '{}'",
                        userMessage.substring(0, Math.min(50, userMessage.length())), e);
                throw new RuntimeException("Failed to create embedding", e);
            }
        });
    }

    /**
     * 🎯 Enhanced dynamic threshold cho 3-way classification
     */
    private double calculateDynamicThreshold(Map<String, Double> allScores, double bestScore) {
        List<Double> scores = new ArrayList<>(allScores.values());
        scores.sort(Collections.reverseOrder());

        if (scores.size() < 2) {
            return BASE_CONFIDENCE_THRESHOLD;
        }

        double firstScore = scores.get(0);
        double secondScore = scores.get(1);
        double gap = firstScore - secondScore;

        // Với 3 intent, gap nhỏ hơn nên adjust threshold
        if (gap > 0.12) { // Large gap → confident hơn
            return Math.max(MIN_CONFIDENCE_THRESHOLD, BASE_CONFIDENCE_THRESHOLD - 0.08);
        }

        if (gap < 0.03) { // Small gap → cần confident hơn
            return Math.min(HIGH_CONFIDENCE_THRESHOLD, BASE_CONFIDENCE_THRESHOLD + 0.12);
        }

        return BASE_CONFIDENCE_THRESHOLD;
    }

    /**
     * 📊 Enhanced similarity calculation với error handling
     */
    private double calculateIntentSimilarity(Embedding userEmbedding, String intent) {
        try {
            List<Embedding> intentEmbeddings = getIntentEmbeddings(intent);

            if (intentEmbeddings.isEmpty()) {
                log.warn("⚠️ No embeddings found for intent: {}", intent);
                return 0.0;
            }

            double totalSimilarity = 0.0;
            int validEmbeddings = 0;

            for (Embedding intentEmbedding : intentEmbeddings) {
                try {
                    double similarity = cosineSimilarity(userEmbedding, intentEmbedding);
                    if (!Double.isNaN(similarity) && !Double.isInfinite(similarity)) {
                        totalSimilarity += similarity;
                        validEmbeddings++;
                    }
                } catch (Exception e) {
                    log.warn("⚠️ Error calculating similarity for intent: {}", intent, e);
                }
            }

            return validEmbeddings > 0 ? totalSimilarity / validEmbeddings : 0.0;

        } catch (Exception e) {
            log.error("❌ Error in calculateIntentSimilarity for intent: {}", intent, e);
            return 0.0;
        }
    }

    /**
     * 🛡️ Resilient Embedding Creation with Fallback
     * 
     * Tạo embedding với fallback mechanism để tránh quota issues
     */
    private Embedding createEmbeddingWithFallback(String text) {
        // 1. Nếu được config để sử dụng local embedding
        if (useLocalEmbedding) {
            log.debug("🏠 Using local embedding for: '{}'", text.substring(0, Math.min(30, text.length())));
            return localEmbeddingService.embedText(text);
        }

        // 2. Thử primary embedding model (Vertex AI)
        try {
            Embedding embedding = embeddingModel.embed(text).content();
            if (embedding != null && embedding.vector() != null) {
                return embedding;
            }
        } catch (Exception e) {
            log.warn("⚠️ Primary embedding failed for '{}'. Error: {}",
                    text.substring(0, Math.min(30, text.length())), e.getMessage());

            // 3. Fallback to local embedding if enabled
            if (fallbackEnabled) {
                log.info("🔄 Falling back to local embedding for: '{}'",
                        text.substring(0, Math.min(30, text.length())));
                return localEmbeddingService.embedText(text);
            } else {
                throw new RuntimeException("Embedding creation failed and fallback is disabled", e);
            }
        }

        // 4. Fallback nếu primary trả về null
        if (fallbackEnabled) {
            log.warn("⚠️ Primary embedding returned null, using fallback for: '{}'",
                    text.substring(0, Math.min(30, text.length())));
            return localEmbeddingService.embedText(text);
        }

        throw new RuntimeException("Failed to create embedding for text: " + text);
    }

    /**
     * 🔄 Enhanced cached embeddings với fallback mechanism
     */
    private List<Embedding> getIntentEmbeddings(String intent) {
        return intentEmbeddingsCache.computeIfAbsent(intent, key -> {
            List<String> examples = INTENT_EXAMPLES.get(key);
            if (examples == null) {
                log.warn("⚠️ No examples found for intent: {}", key);
                return new ArrayList<>();
            }

            List<Embedding> embeddings = new ArrayList<>();
            int successCount = 0;
            int fallbackCount = 0;

            for (String example : examples) {
                try {
                    Embedding embedding = createEmbeddingWithFallback(example);
                    if (embedding != null && embedding.vector() != null) {
                        embeddings.add(embedding);
                        successCount++;

                        // Track if we used local embedding
                        if (useLocalEmbedding) {
                            fallbackCount++;
                        }
                    }
                } catch (Exception e) {
                    log.warn("⚠️ Failed to create embedding for example: {} (intent: {})", example, key, e);
                }
            }

            log.info("💾 Cached {} embeddings for intent: {} (from {} examples) - Local: {}/{}",
                    embeddings.size(), key, examples.size(), fallbackCount, successCount);
            return embeddings;
        });
    }

    /**
     * 📐 Enhanced cosine similarity với validation
     */
    private double cosineSimilarity(Embedding a, Embedding b) {
        if (a == null || b == null || a.vector() == null || b.vector() == null) {
            log.warn("⚠️ Null embedding provided for similarity calculation");
            return 0.0;
        }

        float[] vectorA = a.vector();
        float[] vectorB = b.vector();

        if (vectorA.length != vectorB.length) {
            log.warn("⚠️ Vector dimensions mismatch: {} vs {}", vectorA.length, vectorB.length);
            return 0.0;
        }

        if (vectorA.length == 0) {
            log.warn("⚠️ Empty vectors provided");
            return 0.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }

        if (normA == 0.0 || normB == 0.0) {
            log.warn("⚠️ Zero norm vector detected");
            return 0.0;
        }

        double similarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));

        // Clamp to [-1, 1] range
        return Math.max(-1.0, Math.min(1.0, similarity));
    }

    /**
     * 🔍 Enhanced debug với detailed analysis
     */
    public Map<String, Object> debugClassification(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return Map.of("error", "Empty message provided");
        }

        try {
            Embedding userEmbedding = getUserEmbedding(userMessage);
            Map<String, Object> debug = new HashMap<>();
            debug.put("message", userMessage);
            debug.put("messageLength", userMessage.length());
            debug.put("userEmbeddingDimension", userEmbedding.vector().length);
            debug.put("hasStructuredKeywords", containsStructuredKeywords(userMessage));

            Map<String, Map<String, Double>> detailedScores = new HashMap<>();
            Map<String, Double> intentAverages = new HashMap<>();

            for (String intent : INTENT_EXAMPLES.keySet()) {
                Map<String, Double> exampleScores = new HashMap<>();
                List<String> examples = INTENT_EXAMPLES.get(intent);
                double totalScore = 0.0;
                int validScores = 0;

                for (String example : examples) {
                    try {
                        Embedding exampleEmbedding = embeddingModel.embed(example).content();
                        double similarity = cosineSimilarity(userEmbedding, exampleEmbedding);
                        exampleScores.put(example, similarity);

                        if (!Double.isNaN(similarity) && !Double.isInfinite(similarity)) {
                            totalScore += similarity;
                            validScores++;
                        }
                    } catch (Exception e) {
                        exampleScores.put(example, 0.0);
                        log.warn("⚠️ Error in debug for example: {}", example, e);
                    }
                }

                detailedScores.put(intent, exampleScores);
                intentAverages.put(intent, validScores > 0 ? totalScore / validScores : 0.0);
            }

            debug.put("detailedScores", detailedScores);
            debug.put("intentAverages", intentAverages);
            debug.put("cacheSize", userEmbeddingCache.size());
            debug.put("intentCacheSize", intentEmbeddingsCache.size());

            return debug;

        } catch (Exception e) {
            log.error("❌ Error in debug classification", e);
            return Map.of("error", e.getMessage());
        }
    }

    /**
     * 🧹 Cache management
     */
    public void clearUserCache() {
        userEmbeddingCache.clear();
        log.info("🧹 Cleared user embedding cache");
    }

    public void clearIntentCache() {
        intentEmbeddingsCache.clear();
        log.info("🧹 Cleared intent embedding cache");
    }

    public Map<String, Integer> getCacheStats() {
        return Map.of(
                "userCacheSize", userEmbeddingCache.size(),
                "intentCacheSize", intentEmbeddingsCache.size(),
                "totalIntentExamples", INTENT_EXAMPLES.values().stream()
                        .mapToInt(List::size).sum());
    }

    /**
     * 📚 Enhanced learning từ feedback
     */
    public void addTrainingExample(String message, String correctIntent, double confidence) {
        if (confidence >= 0.8 && INTENT_EXAMPLES.containsKey(correctIntent)) {
            log.info("📚 Learning opportunity: '{}' should be classified as {} (confidence: {:.3f})",
                    message, correctIntent, confidence);

            // TODO: Implement online learning
            // Could add to a learning queue for batch updates
        }
    }

    /**
     * 🎯 Enhanced Result class
     */
    public static class IntentClassificationResult {
        private final String intent;
        private final double confidence;
        private final boolean isConfident;
        private final Map<String, Double> allScores;
        private final String additionalInfo;

        public IntentClassificationResult(String intent, double confidence, boolean isConfident,
                Map<String, Double> allScores, String additionalInfo) {
            this.intent = intent;
            this.confidence = confidence;
            this.isConfident = isConfident;
            this.allScores = allScores != null ? new HashMap<>(allScores) : new HashMap<>();
            this.additionalInfo = additionalInfo;
        }

        // Constructor for backward compatibility
        public IntentClassificationResult(String intent, double confidence, boolean isConfident,
                Map<String, Double> allScores) {
            this(intent, confidence, isConfident, allScores, "");
        }

        // Getters
        public String getIntent() {
            return intent;
        }

        public double getConfidence() {
            return confidence;
        }

        public boolean isConfident() {
            return isConfident;
        }

        public Map<String, Double> getAllScores() {
            return new HashMap<>(allScores);
        }

        public String getAdditionalInfo() {
            return additionalInfo;
        }

        public String getSecondBestIntent() {
            return allScores.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals(intent))
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("UNKNOWN");
        }

        public double getConfidenceGap() {
            double bestScore = confidence;
            double secondBest = allScores.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals(intent))
                    .mapToDouble(Map.Entry::getValue)
                    .max()
                    .orElse(0.0);
            return bestScore - secondBest;
        }

        @Override
        public String toString() {
            return String.format("Intent: %s (%.3f) - Confident: %s - Gap: %.3f - %s",
                    intent, confidence, isConfident, getConfidenceGap(), additionalInfo);
        }
    }
}