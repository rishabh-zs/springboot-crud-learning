package com.zs.practice1.Service;

import com.zs.practice1.dao.ProductJpaRepository;
import com.zs.practice1.exception.ProductAlreadyExistsException;
import com.zs.practice1.exception.ProductNotFoundException;
import com.zs.practice1.model.Product;
import com.zs.practice1.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class ProductServiceTest {
    private ProductJpaRepository repo;
    private ProductService service;

    static Stream<Product> nonValidatedPayloads() {
        return Stream.of(
                new Product(1, "", 1.0, 1),
                new Product(1, "Phone", -1.0, 1),
                new Product(1, "Phone", 1.0, 0)
        );
    }

    static Stream<Arguments> nonPositiveIds() {
        return Stream.of(arguments(0), arguments(-1));
    }

    @BeforeEach
    void setUp() {
        repo = mock(ProductJpaRepository.class);
        service = new ProductService(repo);
    }

    @Test
    void getAllProducts_ReturnsRepositoryData() {
        List<Product> products = List.of(new Product(1, "Phone", 999.99, 1));
        when(repo.findAllByOrderById()).thenReturn(products);
        assertSame(products, service.getAllProducts());
        verify(repo).findAllByOrderById();
    }

    @Test
    void addProduct_SetsNullIdAndSaves() {
        Product input = new Product(10, "Phone", 999.99, 1);
        Product saved = new Product(1, "Phone", 999.99, 1);
        when(repo.save(any(Product.class))).thenReturn(saved);
        assertSame(saved, service.addProduct(input));
        assertNull(input.getId());
    }

    @Test
    void addProduct_NullPayload_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> service.addProduct(null));
        verifyNoInteractions(repo);
    }

    @ParameterizedTest
    @MethodSource("nonValidatedPayloads")
    void addProduct_NonValidatedPayloads_DelegateToRepository(Product product) {
        assertDoesNotThrow(() -> service.addProduct(product));
        verify(repo).save(product);
        assertNull(product.getId());
    }

    @Test
    void addProduct_DuplicateName_ThrowsDomainException() {
        Product input = new Product(1, "Phone", 999.99, 1);
        doThrow(new DataIntegrityViolationException("dup")).when(repo).save(input);
        ProductAlreadyExistsException ex = assertThrows(ProductAlreadyExistsException.class, () -> service.addProduct(input));
        assertEquals("Product already exists: Phone", ex.getMessage());
    }

    @Test
    void deleteProduct_DeletesWhenFound() {
        Product existing = new Product(1, "Phone", 999.99, 1);
        when(repo.findById(1)).thenReturn(Optional.of(existing));
        assertSame(existing, service.deleteProduct(1));
        verify(repo).delete(existing);
    }

    @Test
    void deleteProduct_NullId_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> service.deleteProduct(null));
        verifyNoInteractions(repo);
    }

    @ParameterizedTest
    @MethodSource("nonPositiveIds")
    void deleteProduct_NonPositiveId_ThrowsProductNotFoundException(Integer id) {
        ProductNotFoundException ex = assertThrows(ProductNotFoundException.class, () -> service.deleteProduct(id));
        assertEquals("Product not found with id: " + id, ex.getMessage());
        verify(repo).findById(id);
    }

    @Test
    void deleteProduct_DeleteFailure_WrapsException() {
        Product existing = new Product(1, "Phone", 999.99, 1);
        when(repo.findById(1)).thenReturn(Optional.of(existing));
        doThrow(new DataAccessResourceFailureException("down")).when(repo).delete(existing);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteProduct(1));
        assertEquals("Failed to delete Product from database.", ex.getMessage());
    }

    @Test
    void updateProduct_UpdatesMutableFields() {
        Product existing = new Product(1, "Old", 10.0, 1);
        Product request = new Product(1, "New", 12.0, null);
        when(repo.findById(1)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);
        Product result = service.updateProduct(request);
        assertSame(existing, result);
        assertEquals("New", existing.getName());
        assertEquals(12.0, existing.getPrice());
    }
}
