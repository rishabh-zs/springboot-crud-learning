package com.zs.practice1.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The type Product.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Employee name cannot be blank")
    @NotNull(message = "Employee name cannot be null")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Employee name must contain only letters and spaces")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "price cannot be null")
    @Positive(message = "price must be a positive number")
    private Double price;

    @JsonProperty("categoryId")
    @JsonAlias("category_id")
    @Column(name = "category_id", nullable = false)
    @NotNull(message = "category_id cannot be null")
    @Positive(message = "category_id must be a positive number")
    private Integer category_id;
}
