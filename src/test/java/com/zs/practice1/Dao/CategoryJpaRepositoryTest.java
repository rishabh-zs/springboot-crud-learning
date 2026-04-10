package com.zs.practice1.Dao;

import com.zs.practice1.dao.CategoryJpaRepository;
import com.zs.practice1.model.Category;
import com.zs.practice1.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The type Category jpa repository test.
 */
class CategoryJpaRepositoryTest {

    private CategoryJpaRepository categoryJpaRepository;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        categoryJpaRepository = mock(CategoryJpaRepository.class);
    }

    /**
     * Find all by order by id returns ordered categories.
     */
    @Test
    void findAllByOrderById_ReturnsOrderedCategories() {
        List<Category> categories = List.of(new Category(1, "electronics"), new Category(2, "fashion"));
        when(categoryJpaRepository.findAllByOrderById()).thenReturn(categories);

        List<Category> result = categoryJpaRepository.findAllByOrderById();

        assertEquals(2, result.size());
        assertEquals("electronics", result.getFirst().getName());
        verify(categoryJpaRepository).findAllByOrderById();
    }

    /**
     * Find all products by category id order by id returns products.
     */
    @Test
    void findAllProductsByCategoryIdOrderById_ReturnsProducts() {
        List<Product> products = List.of(
                new Product(1, "laptop", 1000.0, 1),
                new Product(2, "tv", 2000.0, 1)
        );
        when(categoryJpaRepository.findAllProductsByCategoryIdOrderById(1)).thenReturn(products);

        List<Product> result = categoryJpaRepository.findAllProductsByCategoryIdOrderById(1);

        assertEquals(2, result.size());
        assertEquals("laptop", result.getFirst().getName());
        verify(categoryJpaRepository).findAllProductsByCategoryIdOrderById(1);
    }

    /**
     * Save delegates and returns persisted category.
     */
    @Test
    void save_ReturnsSavedCategory() {
        Category input = new Category(null, "electronics");
        Category saved = new Category(1, "electronics");
        when(categoryJpaRepository.save(input)).thenReturn(saved);

        Category result = categoryJpaRepository.save(input);

        assertSame(saved, result);
    }

    /**
     * Exists by id delegates to repository.
     */
    @Test
    void existsById_DelegatesToRepository() {
        when(categoryJpaRepository.existsById(1)).thenReturn(true);
        assertTrue(categoryJpaRepository.existsById(1));
        verify(categoryJpaRepository).existsById(1);
    }
}

