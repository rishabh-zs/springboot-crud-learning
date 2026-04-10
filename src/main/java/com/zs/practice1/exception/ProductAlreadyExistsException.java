package com.zs.practice1.exception;

/**
 * The type Product already exists exception.
 */
public class ProductAlreadyExistsException extends RuntimeException {

    /**
     * Instantiates a new Product already exists exception.
     *
     * @param productName the product name
     */
    public ProductAlreadyExistsException(String productName) {
        super("Product already exists: " + productName);
    }
}

