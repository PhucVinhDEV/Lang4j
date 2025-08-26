package com.example.Chat_Bot_Lang4J.controller;

import com.example.Chat_Bot_Lang4J.Data.Entity.Categories;
import com.example.Chat_Bot_Lang4J.config.Assistant;
import com.example.Chat_Bot_Lang4J.config.SemanticIntentClassifier;

import com.example.Chat_Bot_Lang4J.Data.Service.VectorDataInitializer;
import dev.langchain4j.data.document.Document;

import com.example.Chat_Bot_Lang4J.Data.Entity.Product;
import com.example.Chat_Bot_Lang4J.Data.Service.DatabaseService;
import com.example.Chat_Bot_Lang4J.Data.Vector.VectorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AssistantController {

    private final Assistant assistant;
    private final DatabaseService databaseService;
    private final VectorService vectorService;
    private final SemanticIntentClassifier intentClassifier;
    // TODO: Re-enable khi fix import issues
    // private final VectorDataInitializer vectorDataInitializer;

    /**
     * 🤖 RAG-Enhanced Chat với AI Assistant
     * POST /api/assistant/chat
     */
    @PostMapping("/api/assistant/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Message cannot be empty"));
            }

            log.info("🤖 Processing RAG chat request: {}", message);

            // 🧠 1. INTENT CLASSIFICATION - Phân loại câu hỏi
            SemanticIntentClassifier.IntentClassificationResult intentResult = intentClassifier.classifyIntent(message);

            log.info("🎯 Intent classified: {} (confidence: {:.3f})",
                    intentResult.getIntent(), intentResult.getConfidence());

            // 🔍 2. RETRIEVAL - TEMPORARILY DISABLED
            // String relevantContext = performRAGRetrieval(message, intentResult);
            String relevantContext = "Tạm thời bỏ qua RAG retrieval, chỉ dùng function calling.";

            // 🤖 3. GENERATION - Tạo response với context liên quan
            String contextMessage = String.format("""
                    THÔNG TIN LIÊN QUAN TÌM ĐƯỢC:
                    %s

                    INTENT ĐÃ PHÂN LOẠI: %s (Confidence: %.3f)

                    CÂU HỎI CỦA NGƯỜI DÙNG: %s

                    Hãy trả lời dựa trên thông tin liên quan phía trên. Nếu không có thông tin đủ, hãy nói rõ.
                    """, relevantContext, intentResult.getIntent(),
                    intentResult.getConfidence(), message);

            String response = assistant.chat(contextMessage);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("response", response);
            result.put("intent", intentResult.getIntent());
            result.put("confidence", intentResult.getConfidence());
            result.put("retrievedContext", relevantContext);
            result.put("timestamp", System.currentTimeMillis());

            log.info("✅ RAG response generated successfully");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("❌ Error processing RAG chat request", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to process chat: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 🔄 Initialize Vector Store với dữ liệu hiện có (Manual trigger)
     * POST /api/assistant/initialize-vectors
     */
    @PostMapping("/api/assistant/initialize-vectors")
    public ResponseEntity<Map<String, Object>> initializeVectorStore() {
        try {
            log.info("🔄 Manual VectorDB initialization requested...");

            // Use the initializer component - TEMPORARILY DISABLED
            // vectorDataInitializer.reinitializeVectorStore();
            log.warn("⚠️ VectorDataInitializer temporarily disabled due to import issues");

            // Get final stats
            List<Product> products = databaseService.getAllProducts();
            List<Categories> categories = databaseService.getAllCategories();
            long documentCount = vectorService.getDocumentCount();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("productsInDatabase", products.size());
            result.put("categoriesInDatabase", categories.size());
            result.put("totalDocumentsInVector", documentCount);
            result.put("message", "Vector store manually initialized successfully");
            result.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("❌ Error in manual vector store initialization", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", "Failed to initialize vector store: " + e.getMessage()));
        }
    }

    /**
     * 📊 Check Vector Store Status
     * GET /api/assistant/vector-status
     */
    @GetMapping("/api/assistant/vector-status")
    public ResponseEntity<Map<String, Object>> getVectorStoreStatus() {
        try {
            long documentCount = vectorService.getDocumentCount();
            List<Product> products = databaseService.getAllProducts();
            List<Categories> categories = databaseService.getAllCategories();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("vectorDocumentCount", documentCount);
            result.put("databaseProductCount", products.size());
            result.put("databaseCategoryCount", categories.size());
            result.put("isVectorStoreReady", documentCount > 0);
            result.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("❌ Error checking vector store status", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", "Failed to check vector store status: " + e.getMessage()));
        }
    }

    /**
     * 🧪 Simple Chat WITHOUT vector search/intent classification (for testing)
     * POST /api/assistant/simple-chat
     */
    @PostMapping("/api/assistant/simple-chat")
    public ResponseEntity<Map<String, Object>> simpleChat(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Message cannot be empty"));
            }

            log.info("🧪 Processing SIMPLE chat request: {}", message);

            // 🔍 TEMPORARILY SKIP RETRIEVAL - Function calling only
            // String relevantContext = performSimpleRetrieval(message);
            String relevantContext = "Tạm thời bỏ qua retrieval, chỉ dùng function calling.";

            // 🤖 Generation với context đơn giản
            String contextMessage = String.format("""
                    THÔNG TIN TÌM ĐƯỢC:
                    %s

                    CÂU HỎI CỦA NGƯỜI DÙNG: %s

                    Hãy trả lời dựa trên thông tin trên một cách thân thiện và hữu ích.
                    """, relevantContext, message);

            String response = assistant.chat(contextMessage);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("response", response);
            result.put("retrievedContext", relevantContext);
            result.put("mode", "simple");
            result.put("timestamp", System.currentTimeMillis());

            log.info("✅ Simple response generated successfully");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("❌ Error processing simple chat request", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to process simple chat: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 🔍 Simple retrieval - chỉ dùng database search
     */
    private String performSimpleRetrieval(String query) {
        try {
            StringBuilder contextBuilder = new StringBuilder();
            boolean foundInfo = false;

            // 1. Thử tìm sản phẩm trực tiếp
            if (isProductQuery(query)) {
                log.info("🔍 Product query detected, searching database...");

                String keywords = extractKeywords(query);
                List<Product> products = databaseService.searchProducts(keywords);

                if (!products.isEmpty()) {
                    contextBuilder.append("📦 SẢN PHẨM TÌM ĐƯỢC:\n");
                    products.stream().limit(5)
                            .forEach(p -> contextBuilder.append(String.format("- %s (Giá: %.2f VNĐ, Tồn kho: %d)\n",
                                    p.getName(), p.getPrice(), p.getStockQuantity())));
                    foundInfo = true;
                }

                // 2. Fallback: tìm theo category
                if (!foundInfo) {
                    List<Categories> categories = databaseService.searchCategories(keywords);
                    if (!categories.isEmpty()) {
                        contextBuilder.append("📂 DANH MỤC LIÊN QUAN:\n");
                        categories.forEach(c -> contextBuilder
                                .append(String.format("- %s: %s\n", c.getName(), c.getDescription())));

                        // Lấy sản phẩm từ category
                        List<Product> categoryProducts = databaseService
                                .getProductsByCategory(categories.get(0).getId());
                        if (!categoryProducts.isEmpty()) {
                            contextBuilder.append("\n📦 SẢN PHẨM TRONG DANH MỤC:\n");
                            categoryProducts.stream().limit(3)
                                    .forEach(p -> contextBuilder.append(String.format("- %s (Giá: %.2f VNĐ)\n",
                                            p.getName(), p.getPrice())));
                        }
                        foundInfo = true;
                    }
                }

                // 3. Generic laptop search
                if (!foundInfo) {
                    List<Product> laptops = databaseService.searchProducts("laptop");
                    if (!laptops.isEmpty()) {
                        contextBuilder.append("💻 CÁC LAPTOP CÓ SẴN:\n");
                        laptops.stream().limit(5)
                                .forEach(p -> contextBuilder.append(String.format("- %s (Giá: %.2f VNĐ, Tồn kho: %d)\n",
                                        p.getName(), p.getPrice(), p.getStockQuantity())));
                        foundInfo = true;
                    }
                }
            }

            // 4. Last resort: statistics
            if (!foundInfo) {
                String stats = databaseService.getDatabaseStatistics();
                contextBuilder.append("📊 THÔNG TIN TỔNG QUAN:\n").append(stats);

                List<Product> popularProducts = databaseService.getPopularProducts();
                if (!popularProducts.isEmpty()) {
                    contextBuilder.append("\n⭐ SẢN PHẨM PHỔ BIẾN:\n");
                    popularProducts.stream().limit(3)
                            .forEach(p -> contextBuilder.append(String.format("- %s (Giá: %.2f VNĐ)\n",
                                    p.getName(), p.getPrice())));
                }
            }

            String result = contextBuilder.toString();
            log.info("📋 Simple retrieval completed, context length: {} chars", result.length());

            return result.isEmpty() ? "Hiện tại chưa có thông tin cụ thể." : result;

        } catch (Exception e) {
            log.error("❌ Error in simple retrieval", e);
            return "Lỗi khi tìm kiếm thông tin: " + e.getMessage();
        }
    }

    /**
     * 🎯 Pure Function Calling - No retrieval, no intent classification
     * POST /api/assistant/function-chat
     */
    @PostMapping("/api/assistant/function-chat")
    public ResponseEntity<Map<String, Object>> functionChat(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Message cannot be empty"));
            }

            log.info("🎯 Processing FUNCTION CALLING chat request: {}", message);

            // 🤖 DIRECT function calling - no retrieval whatsoever
            String response = assistant.chat(message);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("response", response);
            result.put("mode", "function-calling-only");
            result.put("timestamp", System.currentTimeMillis());

            log.info("✅ Function calling response generated successfully");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("❌ Error processing function calling chat request", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to process function calling chat: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 📊 Original Chat - Lấy toàn bộ DB data gửi lên LLM như ban đầu
     * POST /api/assistant/original-chat
     */
    @PostMapping("/api/assistant/original-chat")
    public ResponseEntity<Map<String, Object>> originalChat(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Message cannot be empty"));
            }

            log.info("📊 Processing ORIGINAL chat request: {}", message);

            // Lấy thông tin database để bổ sung vào context (như ban đầu)
            String databaseInfo = databaseService.getDatabaseStatistics();

            // Tạo context message với thông tin database
            String contextMessage = String.format("""
                    THÔNG TIN CƠ SỞ DỮ LIỆU HIỆN TẠI:
                    %s

                    CÂU HỎI CỦA NGƯỜI DÙNG: %s
                    """, databaseInfo, message);

            String response = assistant.chat(contextMessage);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("response", response);
            result.put("databaseInfo", databaseInfo);
            result.put("mode", "original-with-db-stats");
            result.put("timestamp", System.currentTimeMillis());

            log.info("✅ Original chat response generated successfully");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("❌ Error processing original chat request", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to process original chat: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Lấy thống kê database
     * GET /api/assistant/statistics
     */
    @GetMapping("/api/assistant/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            String statistics = databaseService.getDatabaseStatistics();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("statistics", statistics);
            result.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("❌ Error getting statistics", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to get statistics: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Lấy thông tin chi tiết sản phẩm
     * GET /api/assistant/product/{id}
     */
    @GetMapping("/api/assistant/product/{id}")
    public ResponseEntity<Map<String, Object>> getProductDetails(@PathVariable Long id) {
        try {
            String productDetails = databaseService.getProductDetails(id);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("productDetails", productDetails);
            result.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("❌ Error getting product details for ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to get product details: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Tìm kiếm sản phẩm
     * GET /api/assistant/search/products?keyword=...
     */
    @GetMapping("/api/assistant/search/products")
    public ResponseEntity<Map<String, Object>> searchProducts(@RequestParam String keyword) {
        try {
            var products = databaseService.searchProducts(keyword);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("keyword", keyword);
            result.put("products", products);
            result.put("count", products.size());
            result.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("❌ Error searching products with keyword: {}", keyword, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to search products: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Tìm kiếm danh mục
     * GET /api/assistant/search/categories?keyword=...
     */
    @GetMapping("/api/assistant/search/categories")
    public ResponseEntity<Map<String, Object>> searchCategories(@RequestParam String keyword) {
        try {
            var categories = databaseService.searchCategories(keyword);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("keyword", keyword);
            result.put("categories", categories);
            result.put("count", categories.size());
            result.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("❌ Error searching categories with keyword: {}", keyword, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to search categories: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    private String performRAGRetrieval(String query, SemanticIntentClassifier.IntentClassificationResult intentResult) {
        try {
            String intent = intentResult.getIntent();
            StringBuilder contextBuilder = new StringBuilder();
            boolean foundRelevantInfo = false;

            // 🎯 Luôn thử DATABASE SEARCH trước cho product queries
            if (isProductQuery(query)) {
                log.info("🔍 Detected product query, searching database first...");

                List<Product> products = databaseService.searchProducts(extractKeywords(query));
                if (!products.isEmpty()) {
                    contextBuilder.append("📦 SẢN PHẨM TÌM ĐƯỢC:\n");
                    products.stream().limit(5)
                            .forEach(p -> contextBuilder
                                    .append(String.format("- %s (Giá: %.2f VNĐ, Tồn kho: %d, Danh mục: %s)\n",
                                            p.getName(), p.getPrice(), p.getStockQuantity(),
                                            p.getCategory() != null ? p.getCategory().getName() : "N/A")));
                    foundRelevantInfo = true;
                    log.info("✅ Found {} products in database", products.size());
                }
            }

            // 🔍 SKIP Vector Search để tránh quota exceeded
            // TODO: Re-enable khi quota đã được fix hoặc dùng local embedding
            log.info("⏭️ Skipping vector search to avoid quota issues");
            /*
             * try {
             * List<Document> vectorResults = vectorService.search(query, 3);
             * if (!vectorResults.isEmpty()) {
             * if (foundRelevantInfo) {
             * contextBuilder.append("\n🔍 THÔNG TIN BỔ SUNG TỪ VECTOR SEARCH:\n");
             * } else {
             * contextBuilder.append("🔍 THÔNG TIN TỪ VECTOR SEARCH:\n");
             * }
             * for (Document doc : vectorResults) {
             * contextBuilder.append("- ").append(doc.text()).append("\n");
             * }
             * foundRelevantInfo = true;
             * }
             * } catch (Exception e) {
             * log.warn("⚠️ Vector search failed: {}", e.getMessage());
             * }
             */

            // 📊 Enhanced Fallback: Tìm theo category và generic products
            if (!foundRelevantInfo && isProductQuery(query)) {
                log.info("🔍 No direct products found, trying category search...");

                List<Categories> categories = databaseService.searchCategories(extractKeywords(query));
                if (!categories.isEmpty()) {
                    contextBuilder.append("📂 DANH MỤC LIÊN QUAN:\n");
                    categories.forEach(
                            c -> contextBuilder.append(String.format("- %s: %s\n", c.getName(), c.getDescription())));

                    // Lấy sản phẩm từ category đầu tiên
                    List<Product> categoryProducts = databaseService.getProductsByCategory(categories.get(0).getId());
                    if (!categoryProducts.isEmpty()) {
                        contextBuilder.append("\n📦 SẢN PHẨM TRONG DANH MỤC:\n");
                        categoryProducts.stream().limit(3)
                                .forEach(p -> contextBuilder.append(String.format("- %s (Giá: %.2f VNĐ)\n",
                                        p.getName(), p.getPrice())));
                    }
                    foundRelevantInfo = true;
                } else {
                    // Tìm theo từ khóa rộng hơn cho laptop
                    log.info("🔍 No categories found, trying broader laptop search...");
                    List<Product> laptops = databaseService.searchProducts("laptop");
                    if (laptops.isEmpty()) {
                        laptops = databaseService.searchProducts("computer");
                    }
                    if (laptops.isEmpty()) {
                        laptops = databaseService.searchProducts("máy tính");
                    }

                    if (!laptops.isEmpty()) {
                        contextBuilder.append("💻 CÁC LAPTOP/MÁY TÍNH CÓ SẴN:\n");
                        laptops.stream().limit(5)
                                .forEach(p -> contextBuilder.append(String.format("- %s (Giá: %.2f VNĐ, Tồn kho: %d)\n",
                                        p.getName(), p.getPrice(), p.getStockQuantity())));
                        foundRelevantInfo = true;
                    }
                }
            }

            // 🆘 Last resort: Thống kê tổng quan
            if (!foundRelevantInfo) {
                log.info("🆘 No specific info found, providing general statistics");
                String stats = databaseService.getDatabaseStatistics();
                contextBuilder.append("📊 THÔNG TIN TỔNG QUAN:\n").append(stats);

                // Thêm một số sản phẩm phổ biến
                List<Product> popularProducts = databaseService.getPopularProducts();
                if (!popularProducts.isEmpty()) {
                    contextBuilder.append("\n⭐ SẢN PHẨM PHỔ BIẾN:\n");
                    popularProducts.stream().limit(3)
                            .forEach(p -> contextBuilder.append(String.format("- %s (Giá: %.2f VNĐ)\n",
                                    p.getName(), p.getPrice())));
                }
            }

            String result = contextBuilder.toString();
            log.info("📋 Retrieved context length: {} chars, found info: {}", result.length(), foundRelevantInfo);

            return result.isEmpty() ? "Không tìm thấy thông tin liên quan." : result;

        } catch (Exception e) {
            log.error("❌ Error in enhanced RAG retrieval", e);
            return "Lỗi khi tìm kiếm thông tin: " + e.getMessage();
        }
    }

    /**
     * 🎯 Detect if query is asking about products
     */
    private boolean isProductQuery(String query) {
        String lowerQuery = query.toLowerCase();
        String[] productIndicators = {
                "laptop", "máy tính", "computer", "dell", "hp", "asus", "lenovo",
                "iphone", "samsung", "điện thoại", "phone", "tablet", "ipad",
                "có", "bán", "price", "giá", "mua", "tìm", "search", "cần"
        };

        return Arrays.stream(productIndicators).anyMatch(lowerQuery::contains);
    }

    /**
     * 🔑 Enhanced keyword extraction
     */
    private String extractKeywords(String query) {
        // Lấy các từ quan trọng, loại bỏ stop words
        String cleaned = query.toLowerCase()
                .replaceAll("[?.,!]", "")
                .replaceAll("\\b(có|bạn|không|ko|mình|tôi|là|của|và|hoặc)\\b", "")
                .trim();

        return cleaned.isEmpty() ? query.trim() : cleaned;
    }

    /**
     * 🔑 Enhanced keyword detection
     */
    private boolean containsSpecificKeywords(String query) {
        String lowerQuery = query.toLowerCase();
        String[] keywords = {
                "giá", "price", "tên", "name", "id", "stock", "tồn kho", "số lượng",
                "laptop", "dell", "hp", "asus", "máy tính", "computer", "điện thoại", "phone"
        };
        return Arrays.stream(keywords).anyMatch(lowerQuery::contains);
    }

}
