package com.zs.practice1.service;

import com.zs.practice1.dao.ProductJpaRepository;
import com.zs.practice1.exception.ProductAlreadyExistsException;
import com.zs.practice1.exception.ProductNotFoundException;
import com.zs.practice1.model.Product;
import com.zs.practice1.util.LoggerUtil;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Objects;

/**
 * The type Product service.
 */
@Service
@CacheConfig(cacheNames = "products")
public class ProductService {
    private static final Logger log = LoggerUtil.getLogger(ProductService.class);
    private final ProductJpaRepository productJpaRepository;

    /**
     * Instantiates a new Product service.
     *
     * @param productJpaRepository the product dao
     */
    public ProductService(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    /**
     * Gets all products.
     *
     * @return the all products
     */
    @Observed(name = "product.service", contextualName = "Fetch All Products")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Cacheable(key = "'all'", unless = "#result == null")
    public List<Product> getAllProducts() {
        List<Product> products;

        try {
            products = productJpaRepository.findAllByOrderById();
        } catch (DataAccessException ex) {
            throw new IllegalArgumentException("Failed to fetch all products", ex);
        }
        return products;
    }

    /**
     * Add product product.
     *
     * @param product the product
     * @return the product
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true, condition = "#result != null")
    @Observed(name = "product.service", contextualName = "Add Product")
    public Product addProduct(Product product) {
        Product addedProduct;

        try {
            product.setId(null);
            addedProduct = productJpaRepository.save(product);
        } catch (DataIntegrityViolationException ex) {
            throw new ProductAlreadyExistsException(product.getName());
        } catch (DataAccessException ex) {
            throw new RuntimeException("Failed to add product to database.", ex);
        }
        return addedProduct;
    }

    /**
     * Delete product product.
     *
     * @param productId the product id
     * @return the product
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true, condition = "#result != null")
    @Observed(name = "product.service", contextualName = "Delete Product")
    public Product deleteProduct(Integer productId) {
        Integer id = Math.toIntExact(productId);
        Product existingProduct = productJpaRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        try {
            productJpaRepository.delete(existingProduct);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Failed to delete Product from database.", ex);
        }
        return existingProduct;
    }

    /**
     * Update product product.
     *
     * @param product the product
     * @return the product
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true, condition = "#result != null")
    @Observed(name = "product.service", contextualName = "Update Product")
    public Product updateProduct(Product product) {
        Objects.requireNonNull(product, "Product payload cannot be null");
        if (product.getId() == null || product.getId() <= 0) {
            throw new IllegalArgumentException("Product id must be a positive number.");
        }

        Product existingProduct = productJpaRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found for id: " + product.getId()));

        Product updatedProduct;
        try {
            existingProduct.setName(product.getName());
            existingProduct.setPrice(product.getPrice());
            updatedProduct = productJpaRepository.save(existingProduct);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Failed to update product in database.", ex);
        }
        return updatedProduct;
    }
}
