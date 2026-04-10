package com.zs.practice1.exception;

/**
 * The type Category not found exception.
 */
public class CategoryNotFoundException extends RuntimeException {

    /**
     * Instantiates a new Category not found exception.
     *
     * @param message the message
     */
    public CategoryNotFoundException(String message) {
        super(message);
    }

}
