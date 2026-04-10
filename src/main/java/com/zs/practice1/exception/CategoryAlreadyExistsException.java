package com.zs.practice1.exception;

/**
 * The type Category already exists exception.
 */
public class CategoryAlreadyExistsException extends RuntimeException {

    /**
     * Instantiates a new Category already exists exception.
     *
     * @param message the message
     */
    public CategoryAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

