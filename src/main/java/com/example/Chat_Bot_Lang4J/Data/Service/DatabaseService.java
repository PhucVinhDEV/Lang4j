package com.example.Chat_Bot_Lang4J.Data.Service;

import com.example.Chat_Bot_Lang4J.Data.Entity.Categories;
import com.example.Chat_Bot_Lang4J.Data.Entity.Product;
import com.example.Chat_Bot_Lang4J.Data.Repository.CategoriesRepository;
import com.example.Chat_Bot_Lang4J.Data.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseService {

    private final CategoriesRepository categoriesRepository;
    private final ProductRepository productRepository;

    // ========== CATEGORIES OPERATIONS ==========

    /**
     * Lấy tất cả categories
     */
    public List<Categories> getAllCategories() {
        return categoriesRepository.findAll();
    }

    /**
     * Tìm category theo ID
     */
    public Optional<Categories> getCategoryById(Long id) {
        return categoriesRepository.findById(id);
    }

    /**
     * Tìm category theo tên
     */
    public Optional<Categories> getCategoryByName(String name) {
        return categoriesRepository.findByName(name);
    }

    /**
     * Tìm categories có chứa từ khóa
     */
    public List<Categories> searchCategories(String keyword) {
        List<Categories> byName = categoriesRepository.findByNameContainingIgnoreCase(keyword);
        List<Categories> byDescription = categoriesRepository.findByDescriptionContainingIgnoreCase(keyword);

        // Merge và loại bỏ duplicate
        byName.addAll(byDescription);
        return byName.stream().distinct().toList();
    }

    /**
     * Lấy categories có sản phẩm
     */
    public List<Categories> getCategoriesWithProducts() {
        return categoriesRepository.findCategoriesWithProducts();
    }

    /**
     * Lưu category mới
     */
    public Categories saveCategory(Categories category) {
        return categoriesRepository.save(category);
    }

    // ========== PRODUCTS OPERATIONS ==========

    /**
     * Lấy tất cả sản phẩm
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Tìm sản phẩm theo ID
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Tìm sản phẩm theo tên
     */
    public Optional<Product> getProductByName(String name) {
        return productRepository.findByName(name);
    }

    /**
     * Tìm sản phẩm theo từ khóa
     */
    public List<Product> searchProducts(String keyword) {
        List<Product> byName = productRepository.findByNameContainingIgnoreCase(keyword);
        List<Product> byDescription = productRepository.findByDescriptionContainingIgnoreCase(keyword);

        // Merge và loại bỏ duplicate
        byName.addAll(byDescription);
        return byName.stream().distinct().toList();
    }

    /**
     * Tìm sản phẩm theo category
     */
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    /**
     * Tìm sản phẩm theo khoảng giá
     */
    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    /**
     * Tìm sản phẩm còn hàng
     */
    public List<Product> getAvailableProducts() {
        return productRepository.findByStockQuantityGreaterThan(0);
    }

    /**
     * Tìm sản phẩm phổ biến
     */
    public List<Product> getPopularProducts() {
        return productRepository.findPopularProducts();
    }

    /**
     * Tìm sản phẩm theo nhiều tiêu chí
     */
    public List<Product> getProductsByCriteria(String name, Long categoryId,
            Double minPrice, Double maxPrice, Integer minStock) {
        return productRepository.findProductsByCriteria(name, categoryId, minPrice, maxPrice, minStock);
    }

    /**
     * Lưu sản phẩm mới
     */
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Cập nhật số lượng tồn kho
     */
    public Product updateStockQuantity(Long productId, int newQuantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setStockQuantity(newQuantity);
            return productRepository.save(product);
        }
        throw new RuntimeException("Product not found with ID: " + productId);
    }

    // ========== STATISTICS OPERATIONS ==========

    /**
     * Lấy thống kê tổng quan
     */
    public String getDatabaseStatistics() {
        long totalCategories = categoriesRepository.count();
        long totalProducts = productRepository.count();
        long availableProducts = productRepository.findByStockQuantityGreaterThan(0).size();
        long outOfStockProducts = productRepository.findByStockQuantity(0).size();

        return String.format("""
                📊 THỐNG KÊ CƠ SỞ DỮ LIỆU:
                • Tổng số danh mục: %d
                • Tổng số sản phẩm: %d
                • Sản phẩm còn hàng: %d
                • Sản phẩm hết hàng: %d
                """, totalCategories, totalProducts, availableProducts, outOfStockProducts);
    }

    /**
     * Lấy thông tin chi tiết về một sản phẩm
     */
    public String getProductDetails(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            return String.format("""
                    📦 CHI TIẾT SẢN PHẨM:
                    • ID: %d
                    • Tên: %s
                    • Mô tả: %s
                    • Giá: %.2f VNĐ
                    • Số lượng tồn kho: %d
                    • Danh mục: %s
                    """,
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getStockQuantity(),
                    product.getCategory() != null ? product.getCategory().getName() : "Không có danh mục");
        }
        return "❌ Không tìm thấy sản phẩm với ID: " + productId;
    }
}