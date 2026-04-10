package com.zs.practice1.dao;

import com.zs.practice1.model.Category;
import com.zs.practice1.model.Product;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for Category.
 */
public interface CategoryJpaRepository extends JpaRepository<Category, Integer> {

    /**
     * Find all by order by id list.
     *
     * @return the list
     */
    @Observed(name = "category.database", contextualName = "Find All Categories")
    List<Category> findAllByOrderById();

    /**
     * Find all products by category id order by id list.
     *
     * @param categoryId the category id
     * @return the list
     */
    @Query("SELECT p FROM Product p WHERE p.category_id = :categoryId ORDER BY p.id")
    @Observed(name = "category.database", contextualName = "Find Products By Category Id")
    List<Product> findAllProductsByCategoryIdOrderById(@Param("categoryId") Integer categoryId);
}