package com.nearkart.entity;
import jakarta.validation.constraints.*;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================
    // PRODUCT NAME VALIDATION
    // =========================

    @NotBlank(message = "Product name cannot be empty")
    @Column(name = "product_name")
    private String productName;


    // =========================
    // DESCRIPTION
    // =========================

    private String description;


    // =========================
    // PRICE VALIDATION
    // =========================

    @NotNull(message = "Price cannot be empty")
    @Positive(message = "Price must be greater than 0")
    private Double price;


    // =========================
    // STOCK VALIDATION
    // =========================

    @NotNull(message = "Stock cannot be empty")
    @PositiveOrZero(message = "Stock cannot be negative")
    private Integer stock;


    @Column(name = "image_url")
    private String imageUrl;


    // Product belongs to a Shop
    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;


    // Product belongs to a Category
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


    @Column(name = "created_at")
    private LocalDateTime createdAt;


    // =========================
    // DEFAULT CONSTRUCTOR
    // =========================

    public Product() {
    }


    // =========================
    // GETTERS AND SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }


    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}