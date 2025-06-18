package com.example.Chat_Bot_Lang4J.Data.Repository;

import com.example.Chat_Bot_Lang4J.Data.Entity.Product;
import com.example.Chat_Bot_Lang4J.Data.Entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Tìm sản phẩm theo tên
    Optional<Product> findByName(String name);

    // Tìm sản phẩm có chứa từ khóa trong tên
    List<Product> findByNameContainingIgnoreCase(String name);

    // Tìm sản phẩm có chứa từ khóa trong description
    List<Product> findByDescriptionContainingIgnoreCase(String description);

    // Tìm sản phẩm theo category
    List<Product> findByCategory(Categories category);

    // Tìm sản phẩm theo category ID
    List<Product> findByCategoryId(Long categoryId);

    // Tìm sản phẩm theo khoảng giá
    List<Product> findByPriceBetween(double minPrice, double maxPrice);

    // Tìm sản phẩm có giá dưới mức nhất định
    List<Product> findByPriceLessThanEqual(double maxPrice);

    // Tìm sản phẩm có giá trên mức nhất định
    List<Product> findByPriceGreaterThanEqual(double minPrice);

    // Tìm sản phẩm còn hàng (stock > 0)
    List<Product> findByStockQuantityGreaterThan(int minStock);

    // Tìm sản phẩm hết hàng (stock = 0)
    List<Product> findByStockQuantity(int stockQuantity);

    // Custom query để tìm sản phẩm theo nhiều tiêu chí
    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:minStock IS NULL OR p.stockQuantity >= :minStock)")
    List<Product> findProductsByCriteria(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minStock") Integer minStock);

    // Tìm sản phẩm phổ biến (có ít stock nhất - giả định là bán chạy)
    @Query("SELECT p FROM Product p ORDER BY p.stockQuantity ASC")
    List<Product> findPopularProducts();

    // Đếm số sản phẩm theo category
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") Long categoryId);
}