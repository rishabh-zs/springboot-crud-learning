package com.zs.practice1.Dao;

import com.zs.practice1.dao.ProductJpaRepository;
import com.zs.practice1.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The type Product jpa repository test.
 */
class ProductJpaRepositoryTest {

    private ProductJpaRepository productJpaRepository;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        productJpaRepository = mock(ProductJpaRepository.class);
    }

    /**
     * Find all by order by id returns ordered products.
     */
    @Test
    void findAllByOrderById_ReturnsOrderedProducts() {
        List<Product> products = List.of(
                new Product(1, "laptop", 1000.0, 1),
                new Product(2, "tv", 2000.0, 1)
        );
        when(productJpaRepository.findAllByOrderById()).thenReturn(products);

        List<Product> result = productJpaRepository.findAllByOrderById();

        assertEquals(2, result.size());
        assertEquals("laptop", result.getFirst().getName());
        verify(productJpaRepository).findAllByOrderById();
    }

    /**
     * Find all by category id order by id returns products.
     */
    @Test
    void findAllByCategoryIdOrderById_ReturnsOrderedProducts() {
        List<Product> products = List.of(
                new Product(1, "laptop", 1000.0, 1),
                new Product(2, "tv", 2000.0, 1)
        );
        when(productJpaRepository.findAllByCategoryIdOrderById(1)).thenReturn(products);

        List<Product> result = productJpaRepository.findAllByCategoryIdOrderById(1);

        assertEquals(2, result.size());
        assertEquals("laptop", result.getFirst().getName());
        verify(productJpaRepository).findAllByCategoryIdOrderById(1);
    }

    /**
     * Save returns saved entity.
     */
    @Test
    void save_ReturnsSavedProduct() {
        Product input = new Product(null, "Phone", 999.99, 1);
        Product saved = new Product(1, "Phone", 999.99, 1);
        when(productJpaRepository.save(input)).thenReturn(saved);

        Product result = productJpaRepository.save(input);

        assertSame(saved, result);
    }
}

