package com.zs.practice1.Controller;

import com.zs.practice1.exception.GlobalExceptionHandler;
import com.zs.practice1.controller.ProductController;
import com.zs.practice1.model.Product;
import com.zs.practice1.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The type Product controller test.
 */
public class ProductControllerTest {
    private MockMvc mockMvc;
    private ProductService productService;

    /**
     * Setup.
     */
    @BeforeEach
    public void setup() {
        productService = mock(ProductService.class);
        ProductController productController = new ProductController(productService);
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    /**
     * Gets all products from service.
     *
     * @throws Exception the exception
     */
    @Test
    public void getAllProductsFromService() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(
                new Product(1, "laptop", 1000.0, 1),
                new Product(2, "tv", 2000.0, 1),
                new Product(3, "iPhone", 200.0, 1),
                new Product(4, "FaceCream", 4.50, 2),
                new Product(5, "faceGel", 10.0, 2)));

        mockMvc.perform(get("/products/GetallProducts")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("all product fetched successfully"))
                .andExpect(jsonPath("$.products.length()").value(5))
                .andExpect(jsonPath("$.products[0].name").value("laptop"))
                .andExpect(jsonPath("$.products[0].price").value(1000.0))
                .andExpect(jsonPath("$.products[1].name").value("tv"))
                .andExpect(jsonPath("$.products[1].price").value(2000.0))
                .andExpect(jsonPath("$.products[2].name").value("iPhone"))
                .andExpect(jsonPath("$.products[2].price").value(200.0))
                .andExpect(jsonPath("$.products[3].name").value("FaceCream"))
                .andExpect(jsonPath("$.products[3].price").value(4.50))
                .andExpect(jsonPath("$.products[4].price").value(10.0))
                .andExpect(jsonPath("$.products[4].name").value("faceGel"))
                .andExpect(jsonPath("$.totalProductCount").value(5));

        verify(productService).getAllProducts();
        verifyNoMoreInteractions(productService);
    }

    /**
     * Gets all products from service empty list.
     *
     * @throws Exception the exception
     */
    @Test
    public void getAllProductsFromServiceEmptyList() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of());

        mockMvc.perform(get("/products/GetallProducts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.products.length()").value(0))
                .andExpect(jsonPath("$.totalProductCount").value(0));

        verify(productService).getAllProducts();
        verifyNoMoreInteractions(productService);
    }

    /**
     * Gets all products from service invalid end point.
     *
     * @throws Exception the exception
     */
    @Test
    public void getAllProductsFromServiceInvalidEndPoint() throws Exception {
        mockMvc.perform(get("/products/InvalidEndpoint"))
                .andExpect(status().isNotFound());

        verifyNoInteractions(productService);
    }

    /**
     * Handle add product delegates to service.
     *
     * @throws Exception the exception
     */
    @Test
    public void handleAddProductDelegatesToService() throws Exception {
        when(productService.addProduct(org.mockito.ArgumentMatchers.any(Product.class)))
                .thenReturn(new Product(10, "Phone", 999.99, 1));

        mockMvc.perform(post("/products/aProduct")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Phone",
                                  "price": 999.99,
                                  "categoryId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Product with ID :10 added successfully"))
                .andExpect(jsonPath("$.addedProduct.id").value(10))
                .andExpect(jsonPath("$.addedProduct.name").value("Phone"))
                .andExpect(jsonPath("$.addedProduct.price").value(999.99))
                .andExpect(jsonPath("$.addedProduct.categoryId").value(1));

        verify(productService).addProduct(org.mockito.ArgumentMatchers.any(Product.class));
        verifyNoMoreInteractions(productService);
    }

    /**
     * Handle delete product delegates to service.
     *
     * @throws Exception the exception
     */
    @Test
    public void handleDeleteProductDelegatesToService() throws Exception {
        when(productService.deleteProduct(1)).thenReturn(new Product(1, "Phone", 999.99, 1));

        mockMvc.perform(delete("/products/dProduct/{productId}", 1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("product with ID :1 deleted successfully"))
                .andExpect(jsonPath("$.deletedProduct.id").value(1))
                .andExpect(jsonPath("$.deletedProduct.name").value("Phone"))
                .andExpect(jsonPath("$.deletedProduct.price").value(999.99))
                .andExpect(jsonPath("$.deletedProduct.categoryId").value(1));

        verify(productService).deleteProduct(1);
        verifyNoMoreInteractions(productService);
    }

    /**
     * Handle delete product returns bad request for negative id.
     *
     * @throws Exception the exception
     */
    @Test
    public void handleDeleteProductReturnsBadRequestForNegativeId() throws Exception {
        mockMvc.perform(delete("/products/dProduct/{productId}", -1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.defaultMessage").value("must be greater than 0"));

        verifyNoInteractions(productService);
    }

    /**
     * Handle delete product returns bad request for non-numeric id.
     *
     * @throws Exception the exception
     */
    @Test
    public void handleDeleteProductReturnsBadRequestForInvalidIdFormat() throws Exception {
        mockMvc.perform(delete("/products/dProduct/abc")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("product id must be a valid integer"));

        verifyNoInteractions(productService);
    }

    /**
     * Handle update product delegates to service.
     *
     * @throws Exception the exception
     */
    @Test
    public void handleUpdateProductDelegatesToService() throws Exception {
        when(productService.updateProduct(org.mockito.ArgumentMatchers.any(Product.class)))
                .thenReturn(new Product(1, "Phone Pro", 1099.99, 1));

        mockMvc.perform(patch("/products/uProduct")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 1,
                                  "name": "Phone Pro",
                                                  "price": 1099.99,
                                                  "categoryId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("product with 1 updated successfully"));

        verify(productService).updateProduct(org.mockito.ArgumentMatchers.any(Product.class));
        verifyNoMoreInteractions(productService);
    }

    /**
     * Handle update product returns not found for invalid id.
     *
     * @throws Exception the exception
     */
    @Test
    public void handleUpdateProductReturnsNotFoundForInvalidId() throws Exception {
        when(productService.updateProduct(org.mockito.ArgumentMatchers.any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product id must be a positive number."));

        mockMvc.perform(patch("/products/uProduct")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "id": -1,
                                  "name": "Phone Pro",
                                                  "price": 1099.99,
                                                  "categoryId": 1
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("product id does not exists"));

        verify(productService).updateProduct(org.mockito.ArgumentMatchers.any(Product.class));
        verifyNoMoreInteractions(productService);
    }
}
