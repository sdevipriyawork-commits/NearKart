package com.nearkart.service;

import com.nearkart.dto.ShopDTO;
import com.nearkart.entity.Shop;
import com.nearkart.exception.ResourceNotFoundException;
import com.nearkart.repository.ShopRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopService {

    private final ShopRepository shopRepository;


    // =========================
    // CONSTRUCTOR
    // =========================

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }


    // =========================
    // CREATE SHOP
    // =========================

    public Shop createShop(Shop shop) {

        return shopRepository.save(shop);
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
            dto.setCategoryId(shop.getCategory().getId());
        }

        // OWNER ID
        if (shop.getOwner() != null) {
            dto.setOwnerId(shop.getOwner().getId());
        }

        return dto;
    }
}