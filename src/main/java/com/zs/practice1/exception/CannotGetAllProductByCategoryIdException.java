package com.zs.practice1.exception;

/**
 * The type Cannot get all product by category id exception.
 */
public class CannotGetAllProductByCategoryIdException extends RuntimeException {

    /**
     * Instantiates a new Cannot get all product by category id exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public CannotGetAllProductByCategoryIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
