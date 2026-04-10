package com.zs.practice1.Controller;

import com.zs.practice1.controller.CategoryController;
import com.zs.practice1.exception.GlobalExceptionHandler;
import com.zs.practice1.model.Category;
import com.zs.practice1.model.Product;
import com.zs.practice1.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The type Category controller test.
 */
class CategoryControllerTest {

    private MockMvc mockMvc;
    private CategoryService categoryService;
    private CategoryController categoryController;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        categoryService = mock(CategoryService.class);
        categoryController = new CategoryController(categoryService);
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    /**
     * Handle get all categories return category from service.
     *
     * @throws Exception the exception
     */
    @Test
    void handleGetAllCategoriesReturnCategoryFromService() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of(
                new Category(1, "electronics"),
                new Category(2, "fashion")
        ));

        mockMvc.perform(get("/categories/GetallCategories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("all category fetched successfully"))
                .andExpect(jsonPath("$.categories.length()").value(2))
                .andExpect(jsonPath("$.categories[0].name").value("electronics"))
                .andExpect(jsonPath("$.categories[1].name").value("fashion"))
                .andExpect(jsonPath("$.totalCategoryCount").value(2));

        verify(categoryService).getAllCategories();
    }

    /**
     * Handle add category delegates to service.
     *
     * @throws Exception the exception
     */
    @Test
    void handleAddCategoryDelegatesToService() throws Exception {
        when(categoryService.addCategory(org.mockito.ArgumentMatchers.any(Category.class)))
                .thenReturn(new Category(10, "electronics"));

        mockMvc.perform(post("/categories/aCategory")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "electronics"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("category added successfully with id: 10"))
                .andExpect(jsonPath("$.addedCategory.id").value(10))
                .andExpect(jsonPath("$.addedCategory.name").value("electronics"));

        verify(categoryService).addCategory(org.mockito.ArgumentMatchers.any(Category.class));
        verifyNoMoreInteractions(categoryService);
    }

    /**
     * Handle get all product by category id returns products from service.
     *
     * @throws Exception the exception
     */
    @Test
    void handleGetAllProductByCategoryIdReturnsProductsFromService() throws Exception {
        when(categoryService.getProductsByCategoryId(1)).thenReturn(List.of(
                new Product(1, "laptop", 4.50, 1),
                new Product(2, "iPhone", 10.0, 1),
                new Product(3, "tv", 10.0, 1)
        ));

        when(categoryService.getProductsByCategoryId(2)).thenReturn(List.of(
                new Product(4, "FaceCream", 4.50, 2),
                new Product(5, "faceGel", 10.0, 2)
        ));

        mockMvc.perform(get("/categories/1/products")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.products.length()").value(3))

                .andExpect(jsonPath("$.products[0].name").value("laptop"))
                .andExpect(jsonPath("$.products[0].price").value(4.50))
                .andExpect(jsonPath("$.products[0].categoryId").value(1))

                .andExpect(jsonPath("$.products[1].name").value("iPhone"))
                .andExpect(jsonPath("$.products[1].price").value(10.0))
                .andExpect(jsonPath("$.products[1].categoryId").value(1))
                .andExpect(jsonPath("$.totalProductCountInCategory").value(3));


        mockMvc.perform(get("/categories/2/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.products.length()").value(2))

                .andExpect(jsonPath("$.products[0].name").value("FaceCream"))
                .andExpect(jsonPath("$.products[0].price").value(4.5))
                .andExpect(jsonPath("$.products[0].categoryId").value(2))

                .andExpect(jsonPath("$.products[1].name").value("faceGel"))
                .andExpect(jsonPath("$.products[1].price").value(10.0))
                .andExpect(jsonPath("$.products[1].categoryId").value(2))
                .andExpect(jsonPath("$.totalProductCountInCategory").value(2));

        verify(categoryService).getProductsByCategoryId(1);
        verify(categoryService).getProductsByCategoryId(2);
    }

    /**
     * Handle delete category delegates to service.
     *
     * @throws Exception the exception
     */
    @Test
    void handleDeleteCategoryDelegatesToService() throws Exception {
        when(categoryService.deleteCategory(1)).thenReturn(new Category(1, "electronics"));

        mockMvc.perform(delete("/categories/dCategory/{categoryId}", 1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("category deleted with id :1"))
                .andExpect(jsonPath("$.deletedCategory.id").value(1))
                .andExpect(jsonPath("$.deletedCategory.name").value("electronics"));

        verify(categoryService).deleteCategory(1);
        verifyNoMoreInteractions(categoryService);
    }

    /**
     * Handle get all product by category id returns bad request for negative category id.
     *
     * @throws Exception the exception
     */
    @Test
    void handleGetAllProductByCategoryIdReturnsBadRequestForNegativeCategoryId() throws Exception {
        mockMvc.perform(get("/categories/-1/products"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.defaultMessage").value("must be greater than 0"));

        verifyNoInteractions(categoryService);
    }

    /**
     * Handle update category delegates to service.
     *
     * @throws Exception the exception
     */
    @Test
    void handleUpdateCategoryDelegatesToService() throws Exception {
        when(categoryService.updateCategory(org.mockito.ArgumentMatchers.any(Category.class)))
                .thenReturn(new Category(1, "electronics updated"));

        mockMvc.perform(patch("/categories/uCategory")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 1,
                                  "name": "electronics updated"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("category updated with ID1"));

        verify(categoryService).updateCategory(org.mockito.ArgumentMatchers.any(Category.class));
        verifyNoMoreInteractions(categoryService);
    }

    /**
     * Handle update category returns not found for invalid id.
     *
     * @throws Exception the exception
     */
    @Test
    void handleUpdateCategoryReturnsNotFoundForInvalidId() throws Exception {
        when(categoryService.updateCategory(org.mockito.ArgumentMatchers.any(Category.class)))
                .thenThrow(new IllegalArgumentException("Category id must be a positive number."));

        mockMvc.perform(patch("/categories/uCategory")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "id": -1,
                                  "name": "electronics updated"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("category id does not exists"));

        verify(categoryService).updateCategory(org.mockito.ArgumentMatchers.any(Category.class));
        verifyNoMoreInteractions(categoryService);
    }
}
