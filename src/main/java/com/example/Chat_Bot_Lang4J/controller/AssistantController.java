package com.example.Chat_Bot_Lang4J.controller;

import com.example.Chat_Bot_Lang4J.config.Assistant;
import com.example.Chat_Bot_Lang4J.Data.Service.DatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AssistantController {

    private final Assistant assistant;
    private final DatabaseService databaseService;

    /**
     * Chat với AI Assistant
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

            log.info("🤖 Processing chat request: {}", message);

            // Lấy thông tin database để bổ sung vào context
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
            result.put("timestamp", System.currentTimeMillis());

            log.info("✅ Chat response generated successfully");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("❌ Error processing chat request", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to process chat: " + e.getMessage());
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
}
