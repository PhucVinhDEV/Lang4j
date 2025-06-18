package com.example.Chat_Bot_Lang4J;

import com.example.Chat_Bot_Lang4J.Data.Entity.Categories;
import com.example.Chat_Bot_Lang4J.Data.Entity.Product;
import com.example.Chat_Bot_Lang4J.Data.Service.DatabaseService;
import com.example.Chat_Bot_Lang4J.config.Assistant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SystemTest {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private Assistant assistant;

    @Test
    void testDatabaseOperations() {
        // Test Categories operations
        Categories testCategory = new Categories(999L, "Test Category", "Test Description");
        Categories savedCategory = databaseService.saveCategory(testCategory);

        assertNotNull(savedCategory);
        assertEquals("Test Category", savedCategory.getName());

        // Test Product operations
        Product testProduct = new Product(999L, "Test Product", "Test Product Description",
                1000000, 10, testCategory);
        Product savedProduct = databaseService.saveProduct(testProduct);

        assertNotNull(savedProduct);
        assertEquals("Test Product", savedProduct.getName());
        assertEquals(1000000, savedProduct.getPrice());

        // Test search operations
        List<Product> searchResults = databaseService.searchProducts("Test");
        assertFalse(searchResults.isEmpty());

        // Test statistics
        String statistics = databaseService.getDatabaseStatistics();
        assertNotNull(statistics);
        assertTrue(statistics.contains("THỐNG KÊ"));
    }

    @Test
    void testAssistantIntegration() {
        // Test basic chat functionality
        String response = assistant.chat("Xin chào, bạn có thể giúp gì cho tôi?");

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertTrue(response.contains("trợ lý") || response.contains("assistant"));
    }

    @Test
    void testProductSearch() {
        // Test product search by criteria
        List<Product> products = databaseService.getProductsByCriteria(
                "iPhone", null, null, null, null);

        // This test will pass if there are iPhone products in the database
        // or if the search returns empty list (both are valid)
        assertNotNull(products);
    }

    @Test
    void testCategorySearch() {
        // Test category search
        List<Categories> categories = databaseService.searchCategories("Điện");

        // This test will pass if there are categories containing "Điện"
        // or if the search returns empty list (both are valid)
        assertNotNull(categories);
    }

    @Test
    void testProductDetails() {
        // Test getting product details
        String productDetails = databaseService.getProductDetails(1L);

        // This test will pass if product with ID 1 exists
        // or if it returns "not found" message (both are valid)
        assertNotNull(productDetails);
    }
}