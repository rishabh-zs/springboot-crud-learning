package com.zs.practice1.service;

import com.zs.practice1.dao.CategoryJpaRepository;
import com.zs.practice1.exception.CannotGetAllCategoryException;
import com.zs.practice1.exception.CannotGetAllProductByCategoryIdException;
import com.zs.practice1.exception.CategoryAlreadyExistsException;
import com.zs.practice1.exception.CategoryNotFoundException;
import com.zs.practice1.model.Category;
import com.zs.practice1.model.Product;
import com.zs.practice1.util.LoggerUtil;
import io.micrometer.observation.annotation.Observed;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


/**
 * The type Category service.
 */
@Service
@CacheConfig(cacheNames = "categories")
public class CategoryService {
    private static final Logger log = LoggerUtil.getLogger(CategoryService.class);
    private final CategoryJpaRepository categoryJpaRepository;

    /**
     * Instantiates a new Category service.
     *
     * @param categoryJpaRepository the category dao
     */
    public CategoryService(CategoryJpaRepository categoryJpaRepository) {
        this.categoryJpaRepository = categoryJpaRepository;
    }

    /**
     * Gets all categories.
     *
     * @return the all categories
     */
    @Observed(name = "category.service", contextualName = "Fetch All Categories")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Cacheable(key = "'all'", unless = "#result == null")
    public List<Category> getAllCategories() {
        List<Category> categories;

        try {
            categories = categoryJpaRepository.findAllByOrderById();
        } catch (DataRetrievalFailureException ex) {
            throw new CannotGetAllCategoryException("Failed to fetch all categories.", ex);
        }
        return categories;
    }

    /**
     * Add category category.
     *
     * @param category the category
     * @return the category
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true, condition = "#result != null")
    @Observed(name = "category.service", contextualName = "Add Category")
    public Category addCategory(Category category) {
        Category newCat;

        try {
            category.setId(null);
            newCat = categoryJpaRepository.save(category);
        } catch (DataIntegrityViolationException ex) {
            throw new CategoryAlreadyExistsException("Category already exists", ex);
        }
        return newCat;
    }

    /**
     * Gets products by category id.
     *
     * @param categoryId the category id
     * @return the products by category id
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Cacheable(key = "'products:' + #categoryId", unless = "#result == null")
    @Observed(name = "category.service", contextualName = "Get Products By Category")
    public List<Product> getProductsByCategoryId(Integer categoryId) {
        Integer id = Math.toIntExact(categoryId);

        if (!categoryJpaRepository.existsById(id)) {
            throw new CategoryNotFoundException("Category not found for id: " + categoryId);
        }
        List<Product> products;
        try {
            products = categoryJpaRepository.findAllProductsByCategoryIdOrderById(categoryId);
        } catch (DataAccessException ex) {
            throw new CannotGetAllProductByCategoryIdException("Failed to fetch all products for category id.", ex);
        }
        return products;
    }


    /**
     * Delete category.
     *
     * @param categoryId the category id
     * @return the category
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true, condition = "#result != null")
    @Observed(name = "category.service", contextualName = "Delete Category")
    public Category deleteCategory(Integer categoryId) {
        Integer id = Math.toIntExact(categoryId);

        Category existingCategory = categoryJpaRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found for id: " + categoryId));
        try {
            categoryJpaRepository.delete(existingCategory);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Failed to delete category from database.", ex);
        }
        return existingCategory;
    }

    /**
     * Update category.
     *
     * @param category the category
     * @return the category
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true, condition = "#result != null")
    @Observed(name = "category.service", contextualName = "Update Category")
    public Category updateCategory(Category category) {
        Objects.requireNonNull(category, "Category payload cannot be null");
        if (category.getId() == null || category.getId() <= 0) {
            throw new IllegalArgumentException("Category id must be a positive number.");
        }

        Category existingCategory = categoryJpaRepository.findById(category.getId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found for id: " + category.getId()));
        Category updatedCategory;
        try {
            existingCategory.setName(category.getName());
            updatedCategory = categoryJpaRepository.save(existingCategory);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Failed to update category in database.", ex);
        }
        return updatedCategory;
    }
}
