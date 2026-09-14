package com.nearkart.service;

import com.nearkart.dto.ShopDTO;
import com.nearkart.entity.Category;
import com.nearkart.entity.Shop;
import com.nearkart.entity.User;
import com.nearkart.exception.ResourceNotFoundException;
import com.nearkart.repository.CategoryRepository;
import com.nearkart.repository.ShopRepository;
import com.nearkart.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;


    // =========================
    // CONSTRUCTOR
    // =========================

    public ShopService(
            ShopRepository shopRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository) {

        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }


    // =========================
    // CREATE SHOP
    // =========================

    public ShopDTO createShop(ShopDTO shopDTO) {

        // =========================
        // FIND OWNER
        // =========================

        if (shopDTO.getOwnerId() == null) {

            throw new RuntimeException(
                    "Owner ID is required"
            );
        }

        User owner = userRepository
                .findById(shopDTO.getOwnerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Owner not found with id: "
                                        + shopDTO.getOwnerId()
                        )
                );


        // =========================
        // FIND CATEGORY
        // =========================

        Category category = null;

        if (shopDTO.getCategoryId() != null) {

            category = categoryRepository
                    .findById(shopDTO.getCategoryId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Category not found with id: "
                                            + shopDTO.getCategoryId()
                            )
                    );
        }


        // =========================
        // CREATE SHOP ENTITY
        // =========================

        Shop shop = new Shop();

        shop.setShopName(shopDTO.getShopName());
        shop.setAddress(shopDTO.getAddress());
        shop.setPhone(shopDTO.getPhone());
        shop.setImageUrl(shopDTO.getImageUrl());
        shop.setStatus(shopDTO.getStatus());

        // IMPORTANT
        // Set owner relationship
        shop.setOwner(owner);

        // Set category relationship
        shop.setCategory(category);

        // Created time
        shop.setCreatedAt(LocalDateTime.now());


        // =========================
        // SAVE SHOP
        // =========================

        Shop savedShop =
                shopRepository.save(shop);


        // =========================
        // RETURN DTO
        // =========================

        return convertToDTO(savedShop);
    }


    // =========================
    // GET ALL SHOPS
    // =========================

    public List<ShopDTO> getAllShops() {

        return shopRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    // =========================
    // GET SHOP BY ID
    // =========================

    public ShopDTO getShopById(Long id) {

        Shop shop = shopRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Shop not found with id: " + id
                        )
                );

        return convertToDTO(shop);
    }


    // =========================
    // UPDATE SHOP
    // =========================

    public Shop updateShop(
            Long id,
            Shop shopDetails) {

        Shop shop = shopRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Shop not found with id: " + id
                        )
                );

        shop.setShopName(shopDetails.getShopName());
        shop.setAddress(shopDetails.getAddress());
        shop.setPhone(shopDetails.getPhone());
        shop.setImageUrl(shopDetails.getImageUrl());
        shop.setStatus(shopDetails.getStatus());

        if (shopDetails.getCategory() != null) {
            shop.setCategory(shopDetails.getCategory());
        }

        return shopRepository.save(shop);
    }


    // =========================
    // DELETE SHOP
    // =========================

    public void deleteShop(Long id) {

        Shop shop = shopRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Shop not found with id: " + id
                        )
                );

        shopRepository.delete(shop);
    }


    // =========================
    // CONVERT SHOP TO DTO
    // =========================

    private ShopDTO convertToDTO(Shop shop) {

        ShopDTO dto = new ShopDTO();

        dto.setId(shop.getId());
        dto.setShopName(shop.getShopName());
        dto.setAddress(shop.getAddress());
        dto.setPhone(shop.getPhone());
        dto.setImageUrl(shop.getImageUrl());
        dto.setStatus(shop.getStatus());
        dto.setCreatedAt(shop.getCreatedAt());

        // CATEGORY ID
        if (shop.getCategory() != null) {
            dto.setCategoryId(
                    shop.getCategory().getId()
            );
        }

        // OWNER ID
        if (shop.getOwner() != null) {
            dto.setOwnerId(
                    shop.getOwner().getId()
            );
        }

        return dto;
    }
}