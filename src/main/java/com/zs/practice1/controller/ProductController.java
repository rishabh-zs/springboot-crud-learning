package com.zs.practice1.controller;

import com.zs.practice1.model.Product;
import com.zs.practice1.service.ProductService;
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
 * The type Product controller.
 */
@RestController
@RequestMapping("/products")
@Observed(name = "product.controller", contextualName = "Product Controller")
public class ProductController {
    private static final Logger log = LoggerUtil.getLogger(ProductController.class);
    private final ProductService productService;

    /**
     * Instantiates a new Product controller.
     *
     * @param productService the product service
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Handle get all products map.
     *
     * @return the map
     */
    @GetMapping("/GetallProducts")
    public ResponseEntity<Map<String, Object>> handleGetAllProducts() {
        long startTime = System.currentTimeMillis();
        log.debug("/allProducts endpoint was called");
        List<Product> products = productService.getAllProducts();
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "all product fetched successfully");
        response.put("products", products);
        response.put("totalProductCount", products.size());
        response.put("responseTime", responseTime + "ms");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Handle add product map.
     *
     * @param product the product
     * @return the map
     */
    @PostMapping("/aProduct")
    public ResponseEntity<Map<String, Object>> handleAddProduct(@Valid @RequestBody Product product) {
        long startTime = System.currentTimeMillis();
        log.debug("/addProduct endpoint was called");
        Product addedProduct = productService.addProduct(product);
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        Map<String, Object> response = new LinkedHashMap<>();
        Integer id = addedProduct.getId();
        response.put("status", "success");
        response.put("message", "Product with ID :" + id + " added successfully");
        response.put("addedProduct", addedProduct);
        response.put("responseTime", responseTime + "ms");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Handle delete product map.
     *
     * @param productId the body
     * @return the map
     */
    @DeleteMapping("/dProduct/{productId}")
    public ResponseEntity<Map<String, Object>> handleDeleteProduct(@PathVariable @Positive Integer productId) {
        long startTime = System.currentTimeMillis();
        log.debug("/deleteProduct endpoint was called");
        Product deletedProduct = productService.deleteProduct(productId);
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        Map<String, Object> response = new LinkedHashMap<>();
        Integer id = deletedProduct.getId();
        response.put("status", "success");
        response.put("message", "product with ID :" + id + " deleted successfully");
        response.put("deletedProduct", deletedProduct);
        response.put("responseTime", responseTime + "ms");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Handle update product map.
     *
     * @param product the product
     * @return the map
     */
    @PatchMapping("/uProduct")
    public ResponseEntity<Map<String, Object>> handleUpdateProduct(@Valid @RequestBody Product product) {
        long startTime = System.currentTimeMillis();
        log.debug("/updateProduct endpoint was called");
        Product updatedProduct = productService.updateProduct(product);
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "product with " + updatedProduct.getId() + " updated successfully");
        response.put("responseTime", responseTime + "ms");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
