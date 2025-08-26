package com.example.Chat_Bot_Lang4J.Data.Service;

import com.example.Chat_Bot_Lang4J.Data.Entity.Categories;
import com.example.Chat_Bot_Lang4J.Data.Entity.Product;
import com.example.Chat_Bot_Lang4J.Data.Vector.VectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VectorDataInitializer {

    private final DatabaseService databaseService;
    private final VectorService vectorService;


    public void initializeVectorStore() {
        try {
            log.info("🚀 Starting automatic VectorDB initialization...");
            
            // Check if vector store already has data
            long existingDocuments = vectorService.getDocumentCount();
            if (existingDocuments > 0) {
                log.info("✅ VectorDB already has {} documents, skipping initialization", existingDocuments);
                return;
            }

            // Get all data from database
            List<Product> products = databaseService.getAllProducts();
            List<Categories> categories = databaseService.getAllCategories();
            
            log.info("📊 Found {} products and {} categories to index", 
                    products.size(), categories.size());

            if (products.isEmpty() && categories.isEmpty()) {
                log.warn("⚠️ No data found in database to index");
                return;
            }

            // Index products
            int productCount = 0;
            for (Product product : products) {
                try {
                    vectorService.saveProduct(product);
                    productCount++;
                    
                    if (productCount % 10 == 0) {
                        log.info("📦 Indexed {} products...", productCount);
                    }
                } catch (Exception e) {
                    log.warn("⚠️ Failed to index product {}: {}", product.getName(), e.getMessage());
                }
            }

            // Index categories
            int categoryCount = 0;
            for (Categories category : categories) {
                try {
                    vectorService.saveCategory(category);
                    categoryCount++;
                } catch (Exception e) {
                    log.warn("⚠️ Failed to index category {}: {}", category.getName(), e.getMessage());
                }
            }

            log.info("✅ VectorDB initialization completed!");
            log.info("📊 Successfully indexed {} products and {} categories", 
                    productCount, categoryCount);
            
            // Verify the indexing
            long finalDocumentCount = vectorService.getDocumentCount();
            log.info("🔍 Total documents in VectorDB: {}", finalDocumentCount);

        } catch (Exception e) {
            log.error("❌ Failed to initialize VectorDB automatically", e);
            log.info("💡 You can manually initialize using POST /api/assistant/initialize-vectors");
        }
    }

    /**
     * 🔄 Re-initialize VectorDB (for manual trigger)
     */
    public void reinitializeVectorStore() {
        try {
            log.info("🔄 Re-initializing VectorDB...");
            
            // Clear existing data
            vectorService.clearAllDocuments();
            log.info("🗑️ Cleared existing vector data");
            
            // Re-run initialization
            initializeVectorStore();
            
        } catch (Exception e) {
            log.error("❌ Failed to re-initialize VectorDB", e);
        }
    }
}