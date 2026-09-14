package com.nearkart.controller;

import com.nearkart.dto.ProductDTO;
import com.nearkart.entity.Product;
import com.nearkart.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // =========================
    // CREATE PRODUCT
    // =========================

    @PostMapping
    public Product createProduct(
            @Valid @RequestBody ProductDTO productDTO) {

        return productService.createProduct(productDTO);
    }

    // =========================
    // GET ALL PRODUCTS
    // =========================

    @GetMapping
    public List<ProductDTO> getAllProducts() {

        return productService.getAllProducts();
    }

    // =========================
    // GET PRODUCT BY ID
    // =========================

    @GetMapping("/{id}")
    public ProductDTO getProductById(
            @PathVariable Long id) {

        return productService.getProductById(id);
    }

    // =========================
    // UPDATE PRODUCT
    // =========================

    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {

        return productService.updateProduct(id, product);
    }

    // =========================
    // DELETE PRODUCT
    // =========================

    @DeleteMapping("/{id}")
    public void deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);
    }
}