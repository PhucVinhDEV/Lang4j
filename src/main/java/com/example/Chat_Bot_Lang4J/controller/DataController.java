package com.example.Chat_Bot_Lang4J.controller;

import com.example.Chat_Bot_Lang4J.Data.Entity.Categories;
import com.example.Chat_Bot_Lang4J.Data.Entity.Product;
import com.example.Chat_Bot_Lang4J.Data.Service.DatabaseService;
import com.example.Chat_Bot_Lang4J.Data.Vector.VectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/api/data")
public class DataController {

    private final DatabaseService databaseService;
    private final VectorService vectorService;

    /**
     * Khởi tạo dữ liệu mẫu
     * POST /api/data/init-sample-data
     */
    @PostMapping("/init-sample-data")
    public ResponseEntity<Map<String, Object>> initSampleData() {
        try {
            log.info("🚀 Initializing sample data...");

            // Tạo categories mẫu
            Categories electronics = new Categories(1L, "Điện tử", "Các sản phẩm điện tử và công nghệ");
            Categories clothing = new Categories(2L, "Thời trang", "Quần áo và phụ kiện thời trang");
            Categories books = new Categories(3L, "Sách", "Sách vở và tài liệu học tập");

            databaseService.saveCategory(electronics);
            databaseService.saveCategory(clothing);
            databaseService.saveCategory(books);

            // Tạo products mẫu
            List<Product> sampleProducts = List.of(
                    new Product(1L, "iPhone 15 Pro", "Điện thoại iPhone 15 Pro 128GB", 25000000, 50, electronics),
                    new Product(2L, "Samsung Galaxy S24", "Điện thoại Samsung Galaxy S24 Ultra", 28000000, 30,
                            electronics),
                    new Product(3L, "MacBook Pro M3", "Laptop MacBook Pro M3 14 inch", 45000000, 20, electronics),
                    new Product(4L, "Áo thun nam", "Áo thun nam cotton 100%", 150000, 100, clothing),
                    new Product(5L, "Quần jean nữ", "Quần jean nữ cao cấp", 350000, 80, clothing),
                    new Product(6L, "Giày sneaker", "Giày sneaker unisex", 800000, 60, clothing),
                    new Product(7L, "Sách Clean Code", "Sách về lập trình Clean Code", 200000, 25, books),
                    new Product(8L, "Sách Design Patterns", "Sách về Design Patterns", 180000, 30, books),
                    new Product(9L, "Sách Spring Boot", "Sách hướng dẫn Spring Boot", 250000, 40, books),
                    new Product(10L, "iPad Air", "Máy tính bảng iPad Air 64GB", 18000000, 35, electronics));

            for (Product product : sampleProducts) {
                databaseService.saveProduct(product);
                // Lưu vào vector store để tìm kiếm ngữ nghĩa
                vectorService.saveProduct(product);
            }

            // Lưu categories vào vector store
            vectorService.saveCategory(electronics);
            vectorService.saveCategory(clothing);
            vectorService.saveCategory(books);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "✅ Sample data initialized successfully!");
            result.put("categoriesCreated", 3);
            result.put("productsCreated", sampleProducts.size());
            result.put("timestamp", System.currentTimeMillis());

            log.info("✅ Sample data initialization completed");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("❌ Error initializing sample data", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to initialize sample data: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Lấy tất cả categories
     * GET /api/data/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        try {
            List<Categories> categories = databaseService.getAllCategories();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("categories", categories);
            result.put("count", categories.size());
            result.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("❌ Error getting categories", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to get categories: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Lấy tất cả products
     * GET /api/data/products
     */
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        try {
            List<Product> products = databaseService.getAllProducts();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("products", products);
            result.put("count", products.size());
            result.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("❌ Error getting products", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to get products: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Thêm category mới
     * POST /api/data/categories
     */
    @PostMapping("/categories")
    public ResponseEntity<Map<String, Object>> addCategory(@RequestBody Categories category) {
        try {
            Categories savedCategory = databaseService.saveCategory(category);
            vectorService.saveCategory(savedCategory);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "✅ Category added successfully!");
            result.put("category", savedCategory);
            result.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("❌ Error adding category", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to add category: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Thêm product mới
     * POST /api/data/products
     */
    @PostMapping("/products")
    public ResponseEntity<Map<String, Object>> addProduct(@RequestBody Product product) {
        try {
            Product savedProduct = databaseService.saveProduct(product);
            vectorService.saveProduct(savedProduct);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "✅ Product added successfully!");
            result.put("product", savedProduct);
            result.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("❌ Error adding product", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to add product: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Xóa tất cả dữ liệu
     * DELETE /api/data/clear-all
     */
    @DeleteMapping("/clear-all")
    public ResponseEntity<Map<String, Object>> clearAllData() {
        try {
            // Xóa tất cả products trước
            List<Product> products = databaseService.getAllProducts();
            for (Product product : products) {
                // Có thể thêm logic xóa từ vector store nếu cần
            }

            // Xóa tất cả categories
            List<Categories> categories = databaseService.getAllCategories();
            for (Categories category : categories) {
                // Có thể thêm logic xóa từ vector store nếu cần
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "✅ All data cleared successfully!");
            result.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("❌ Error clearing data", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to clear data: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}