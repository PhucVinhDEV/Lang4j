package com.example.Chat_Bot_Lang4J.Data.Repository;

import com.example.Chat_Bot_Lang4J.Data.Entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {

    // Tìm category theo tên
    Optional<Categories> findByName(String name);

    // Tìm categories có chứa từ khóa trong tên
    List<Categories> findByNameContainingIgnoreCase(String name);

    // Tìm categories có chứa từ khóa trong description
    List<Categories> findByDescriptionContainingIgnoreCase(String description);

    // Custom query để tìm categories với số lượng sản phẩm
    @Query("SELECT c FROM Categories c LEFT JOIN c.products p GROUP BY c HAVING COUNT(p) > 0")
    List<Categories> findCategoriesWithProducts();

    // Tìm categories theo số lượng sản phẩm
    @Query("SELECT c FROM Categories c LEFT JOIN c.products p GROUP BY c HAVING COUNT(p) >= :minProductCount")
    List<Categories> findCategoriesWithMinProductCount(@Param("minProductCount") int minProductCount);
}