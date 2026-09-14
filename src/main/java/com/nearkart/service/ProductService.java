package com.nearkart.service;

import com.nearkart.dto.ProductDTO;
import com.nearkart.entity.Category;
import com.nearkart.entity.Product;
import com.nearkart.entity.Shop;
import com.nearkart.exception.ResourceNotFoundException;
import com.nearkart.repository.CategoryRepository;
import com.nearkart.repository.ProductRepository;
import com.nearkart.repository.ShopRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;


    // =========================
    // CONSTRUCTOR
    // =========================

    public ProductService(
            ProductRepository productRepository,
            ShopRepository shopRepository,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
        this.categoryRepository = categoryRepository;
    }


    // =========================
    // CREATE PRODUCT
    // =========================

    public Product createProduct(ProductDTO productDTO) {

        // Find Shop using shopId
        Shop shop = shopRepository.findById(productDTO.getShopId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Shop not found with id: "
                                        + productDTO.getShopId()
                        )
                );


        // Find Category using categoryId
        Category category = null;

        if (productDTO.getCategoryId() != null) {

            category = categoryRepository
                    .findById(productDTO.getCategoryId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Category not found with id: "
                                            + productDTO.getCategoryId()
                            )
                    );
        }


        // Create Product
        Product product = new Product();

        product.setProductName(productDTO.getProductName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setImageUrl(productDTO.getImageUrl());

        // ⭐ IMPORTANT MAPPING
        product.setShop(shop);

        // ⭐ CATEGORY MAPPING
        product.setCategory(category);

        // Set creation time
        product.setCreatedAt(LocalDateTime.now());


        // Save Product
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