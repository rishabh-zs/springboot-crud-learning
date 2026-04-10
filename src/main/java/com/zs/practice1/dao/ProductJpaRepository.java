package com.zs.practice1.dao;

import com.zs.practice1.model.Product;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for Product.
 */
public interface ProductJpaRepository extends JpaRepository<Product, Integer> {

    @Observed(name = "product.database", contextualName = "Find All Products")
    List<Product> findAllByOrderById();

    @Query("SELECT p FROM Product p WHERE p.category_id = :categoryId ORDER BY p.id")
    @Observed(name = "product.database", contextualName = "Find Products By Category Id")
    List<Product> findAllByCategoryIdOrderById(@Param("categoryId") Integer categoryId);
}