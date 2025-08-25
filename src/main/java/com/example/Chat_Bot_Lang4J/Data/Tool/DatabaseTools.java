package com.example.Chat_Bot_Lang4J.Data.Tool;

import com.example.Chat_Bot_Lang4J.Data.Entity.Categories;
import com.example.Chat_Bot_Lang4J.Data.Entity.Product;
import com.example.Chat_Bot_Lang4J.Data.Service.DatabaseService;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseTools {

    private final DatabaseService databaseService;

    @Tool("Get database statistics including total products, categories, and stock information")
    public String getDatabaseStatistics() {
        log.info("🔍 Tool called: getDatabaseStatistics");
        return databaseService.getDatabaseStatistics();
    }

    @Tool("Find a specific product by its exact ID number")
    public String findProductById(Long productId) {
        log.info("🔍 Tool called: findProductById({})", productId);
        
        Optional<Product> product = databaseService.getProductById(productId);
        if (product.isPresent()) {
            Product p = product.get();
            return String.format("""
                ✅ **SẢN PHẨM TÌM THẤY:**
                📦 **ID:** %d
                🏷️ **Tên:** %s
                📝 **Mô tả:** %s
                💰 **Giá:** %.2f VNĐ
                📊 **Tồn kho:** %d
                🏪 **Danh mục:** %s
                """, 
                p.getId(), p.getName(), p.getDescription(), p.getPrice(), 
                p.getStockQuantity(), 
                p.getCategory() != null ? p.getCategory().getName() : "Không có");
        } else {
            return "❌ Không tìm thấy sản phẩm với ID: " + productId;
        }
    }

    @Tool("Search products by name using partial matching (contains)")
    public String searchProductsByName(String productName) {
        log.info("🔍 Tool called: searchProductsByName({})", productName);
        
        List<Product> products = databaseService.searchProducts(productName);
        if (products.isEmpty()) {
            return "❌ Không tìm thấy sản phẩm nào có tên chứa: " + productName;
        }

        StringBuilder result = new StringBuilder();
        result.append(String.format("✅ **TÌM THẤY %d SẢN PHẨM:**\n\n", products.size()));
        
        for (Product p : products.subList(0, Math.min(5, products.size()))) { // Limit to 5 results
            result.append(String.format("""
                📦 **%s** (ID: %d)
                💰 Giá: %.2f VNĐ | 📊 Tồn kho: %d
                🏪 Danh mục: %s
                ────────────────────────────────
                """, 
                p.getName(), p.getId(), p.getPrice(), p.getStockQuantity(),
                p.getCategory() != null ? p.getCategory().getName() : "Không có"));
        }
        
        if (products.size() > 5) {
            result.append(String.format("... và %d sản phẩm khác\n", products.size() - 5));
        }
        
        return result.toString();
    }

    @Tool("Find products within a specific price range (in VND)")
    public String findProductsByPriceRange(double minPrice, double maxPrice) {
        log.info("🔍 Tool called: findProductsByPriceRange({}, {})", minPrice, maxPrice);
        
        List<Product> products = databaseService.getProductsByPriceRange(minPrice, maxPrice);
        if (products.isEmpty()) {
            return String.format("❌ Không có sản phẩm nào trong khoảng giá %.0f - %.0f VNĐ", minPrice, maxPrice);
        }

        StringBuilder result = new StringBuilder();
        result.append(String.format("✅ **TÌM THẤY %d SẢN PHẨM TRONG KHOẢNG GIÁ %.0f - %.0f VNĐ:**\n\n", 
                products.size(), minPrice, maxPrice));
        
        for (Product p : products.subList(0, Math.min(5, products.size()))) {
            result.append(String.format("""
                📦 **%s** (ID: %d)
                💰 **Giá: %.0f VNĐ** | 📊 Tồn kho: %d
                🏪 Danh mục: %s
                ────────────────────────────────
                """, 
                p.getName(), p.getId(), p.getPrice(), p.getStockQuantity(),
                p.getCategory() != null ? p.getCategory().getName() : "Không có"));
        }
        
        return result.toString();
    }

    @Tool("Get all available categories in the system")
    public String getAllCategories() {
        log.info("🔍 Tool called: getAllCategories");
        
        List<Categories> categories = databaseService.getAllCategories();
        if (categories.isEmpty()) {
            return "❌ Không có danh mục nào trong hệ thống";
        }

        StringBuilder result = new StringBuilder();
        result.append(String.format("✅ **TẤT CẢ %d DANH MỤC:**\n\n", categories.size()));
        
        for (Categories cat : categories) {
            result.append(String.format("""
                🏪 **%s** (ID: %d)
                📝 %s
                ────────────────────────────────
                """, 
                cat.getName(), cat.getId(), cat.getDescription()));
        }
        
        return result.toString();
    }

    @Tool("Find a specific category by its exact ID number")
    public String findCategoryById(Long categoryId) {
        log.info("🔍 Tool called: findCategoryById({})", categoryId);
        
        Optional<Categories> category = databaseService.getCategoryById(categoryId);
        if (category.isPresent()) {
            Categories cat = category.get();
            return String.format("""
                ✅ **DANH MỤC TÌM THẤY:**
                🏪 **ID:** %d
                🏷️ **Tên:** %s
                📝 **Mô tả:** %s
                """, 
                cat.getId(), cat.getName(), cat.getDescription());
        } else {
            return "❌ Không tìm thấy danh mục với ID: " + categoryId;
        }
    }

    @Tool("Find all products in a specific category by category ID")
    public String findProductsByCategoryId(Long categoryId) {
        log.info("🔍 Tool called: findProductsByCategoryId({})", categoryId);
        
        List<Product> products = databaseService.getProductsByCategory(categoryId);
        if (products.isEmpty()) {
            return "❌ Không có sản phẩm nào trong danh mục ID: " + categoryId;
        }

        StringBuilder result = new StringBuilder();
        result.append(String.format("✅ **TÌM THẤY %d SẢN PHẨM TRONG DANH MỤC ID %d:**\n\n", 
                products.size(), categoryId));
        
        for (Product p : products.subList(0, Math.min(5, products.size()))) {
            result.append(String.format("""
                📦 **%s** (ID: %d)
                💰 Giá: %.0f VNĐ | 📊 Tồn kho: %d
                📝 %s
                ────────────────────────────────
                """, 
                p.getName(), p.getId(), p.getPrice(), p.getStockQuantity(), p.getDescription()));
        }
        
        return result.toString();
    }

    @Tool("Count products in a specific category by category name (e.g., 'Điện tử', 'Thời trang')")
    public String countProductsByCategory(String categoryName) {
        log.info("🔍 Tool called: countProductsByCategory({})", categoryName);
        
        Optional<Categories> category = databaseService.getCategoryByName(categoryName);
        if (category.isEmpty()) {
            return "❌ Không tìm thấy danh mục: " + categoryName;
        }
        
        List<Product> products = databaseService.getProductsByCategory(category.get().getId());
        return String.format("📊 **Danh mục '%s' có %d sản phẩm**", categoryName, products.size());
    }


}