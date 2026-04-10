package com.zs.practice1.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The type Global exception handler.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle product already exists response entity.
     *
     * @return the response entity
     */
    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleProductAlreadyExists() {
        return buildError(HttpStatus.CONFLICT, "Products already exists");
    }

    /**
     * Handle category already exists response entity.
     *
     * @return the response entity
     */
    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryAlreadyExists() {
        return buildError(HttpStatus.CONFLICT, "Categories already exists");
    }

    /**
     * Handle product not found response entity.
     *
     * @param ex the exception
     * @return the response entity
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFound(ProductNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handle category not found response entity.
     *
     * @return the response entity
     */
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryNotFound(CategoryNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handle illegal argument response entity.
     *
     * @param ex the ex
     * @return the response entity
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        String message = ex.getMessage();
        if (message != null && message.toLowerCase().contains("product id")) {
            return buildError(HttpStatus.NOT_FOUND, "product id does not exists");
        }
        if (message != null && message.toLowerCase().contains("category id")) {
            return buildError(HttpStatus.NOT_FOUND, "category id does not exists");
        }
        return buildError(HttpStatus.BAD_REQUEST, message != null ? message : "invalid request");
    }

    /**
     * Handle cannot get all category exception response entity.
     *
     * @return the response entity
     */
    @ExceptionHandler(CannotGetAllCategoryException.class)
    public ResponseEntity<Map<String, Object>> handleCannotGetAllCategoryException() {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "cannot get all categories");
    }

    /**
     * Handle cannot get all product by category id exception response entity.
     *
     * @return the response entity
     */
    @ExceptionHandler(CannotGetAllProductByCategoryIdException.class)
    public ResponseEntity<Map<String, Object>> handleCannotGetAllProductByCategoryIdException() {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "cannot get all products by category id");
    }

    /**
     * Handle method argument not valid exception response entity.
     *
     * @param ex the ex
     * @return the response entity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handle constraint violation exception response entity.
     *
     * @param ex the ex
     * @return the response entity
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getMessage();
        if (message != null && message.toLowerCase().contains("productid")) {
            return buildError(HttpStatus.BAD_REQUEST, "product id must be greater than 0");
        }
        return buildError(HttpStatus.BAD_REQUEST, message != null ? message : "invalid request");
    }

    /**
     * Handle method argument type mismatch exception response entity.
     *
     * @param ex the ex
     * @return the response entity
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        if ("productId".equals(ex.getName())) {
            return buildError(HttpStatus.BAD_REQUEST, "product id must be a valid integer");
        }
        return buildError(HttpStatus.BAD_REQUEST, "invalid request parameter");
    }

    /**
     * Handle method-level validation errors for path/query parameters.
     *
     * @param ex the ex
     * @return the response entity
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        String defaultMessage = ex.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .filter(message -> message != null && !message.isBlank())
                .findFirst()
                .orElse("invalid request");

        return buildDefaultMessageError(HttpStatus.BAD_REQUEST, defaultMessage);
    }

    /**
     * Handle malformed JSON payloads.
     *
     * @param ex the ex
     * @return the response entity
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        if (message != null && message.toLowerCase().contains("unexpected character") && message.contains(",")) {
            return buildError(HttpStatus.BAD_REQUEST, "Invalid JSON: provide a valid integer for category id.");
        }
        return buildError(HttpStatus.BAD_REQUEST, "Invalid request body format.");
    }

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus statusCode, String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "error");
        response.put("message", message);
        return ResponseEntity.status(statusCode).body(response);
    }

    private ResponseEntity<Map<String, Object>> buildDefaultMessageError(HttpStatus statusCode, String defaultMessage) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("defaultMessage", defaultMessage);
        return ResponseEntity.status(statusCode).body(response);
    }
}
