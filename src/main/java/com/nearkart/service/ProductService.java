package com.nearkart.service;

import com.nearkart.dto.ProductDTO;
import com.nearkart.entity.Product;
import com.nearkart.exception.ResourceNotFoundException;
import com.nearkart.repository.ProductRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;


    // =========================
    // CONSTRUCTOR
    // =========================

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    // =========================
    // CREATE PRODUCT
    // =========================

    public Product createProduct(Product product) {

        return productRepository.save(product);
    }


    // =========================
    // GET ALL PRODUCTS
    // =========================

    public List<ProductDTO> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    // =========================
    // GET PRODUCT BY ID
    // =========================

    public ProductDTO getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        return convertToDTO(product);
    }


    // =========================
    // UPDATE PRODUCT
    // =========================

    public Product updateProduct(
            Long id,
            Product productDetails) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        product.setProductName(productDetails.getProductName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setImageUrl(productDetails.getImageUrl());

        return productRepository.save(product);
    }


    // =========================
    // DELETE PRODUCT
    // =========================

    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        productRepository.delete(product);
    }


    // =========================
    // CONVERT PRODUCT TO DTO
    // =========================

    public ProductDTO convertToDTO(Product product) {

        ProductDTO dto = new ProductDTO();

        dto.setId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setImageUrl(product.getImageUrl());

        if (product.getShop() != null) {
            dto.setShopId(product.getShop().getId());
        }

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
        }

        return dto;
    }
}