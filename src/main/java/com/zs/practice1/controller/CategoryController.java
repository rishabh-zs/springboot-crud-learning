package com.zs.practice1.controller;

import com.zs.practice1.model.Category;
import com.zs.practice1.model.Product;
import com.zs.practice1.service.CategoryService;
import com.zs.practice1.util.LoggerUtil;
import io.micrometer.observation.annotation.Observed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Category controller.
 */
@RestController
@RequestMapping("/categories")
@Observed(name = "category.controller", contextualName = "Category Controller")
public class CategoryController {
    private static final Logger log = LoggerUtil.getLogger(CategoryController.class);
    private final CategoryService categoryService;

    /**
     * Instantiates a new Category controller.
     *
     * @param categoryService the category service
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Handle get all categories map.
     *
     * @return the map
     */
    @GetMapping("/GetallCategories")
    public ResponseEntity<Map<String, Object>> handleGetAllCategories() {
        long startTime = System.currentTimeMillis();
        log.debug("/GetallCategories endpoint was called");
        List<Category> categories = categoryService.getAllCategories();
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "all category fetched successfully");
        response.put("categories", categories);
        response.put("totalCategoryCount", categories.size());
        response.put("responseTime", responseTime + "ms");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Handle add category map.
     *
     * @param category the category
     * @return the map
     */
    @PostMapping("/aCategory")
    public ResponseEntity<Map<String, Object>> handleAddCategory(@Valid @RequestBody Category category) {
        long startTime = System.currentTimeMillis();
        log.debug("/addCategory endpoint was called");
        Category addedCategory = categoryService.addCategory(category);
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "category added successfully with id: " + addedCategory.getId());
        response.put("addedCategory", addedCategory);
        response.put("responseTime", responseTime + "ms");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Handle get all product by category id map.
     *
     * @param categoryId the category id
     * @return the map
     */
    @GetMapping("/{categoryId}/products")
    public ResponseEntity<Map<String, Object>> handleGetAllProductByCategoryId(@PathVariable @Positive Integer categoryId) {
        long startTime = System.currentTimeMillis();
        log.debug("/{categoryId}/products endpoint was called");
        List<Product> products = categoryService.getProductsByCategoryId(categoryId);
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "products fetched successfully for category id: " + categoryId);
        response.put("products", products);
        response.put("totalProductCountInCategory", products.size());
        response.put("responseTime", responseTime + "ms");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Handle delete category map.
     *
     * @param categoryId the body
     * @return the map
     */
    @DeleteMapping("/dCategory/{categoryId}")
    public ResponseEntity<Map<String, Object>> handleDeleteCategory(@PathVariable @Positive Integer categoryId) {
        long startTime = System.currentTimeMillis();
        Integer catId = categoryId;
        log.debug("/deleteCategory endpoint was called");
        Category deletedCategory = categoryService.deleteCategory(catId);
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "category deleted with id :" + categoryId);
        response.put("deletedCategory", deletedCategory);
        response.put("responseTime", responseTime + "ms");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Handle update category map.
     *
     * @param category the category
     * @return the map
     */
    @PatchMapping("/uCategory")
    public ResponseEntity<Map<String, Object>> handleUpdateCategory(@Valid @RequestBody Category category) {
        long startTime = System.currentTimeMillis();
        log.debug("/updateCategory endpoint was called");
        Category updatedCategory = categoryService.updateCategory(category);
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "category updated with ID" + updatedCategory.getId());
        response.put("responseTime", responseTime + "ms");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
