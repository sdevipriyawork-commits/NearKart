package com.nearkart.controller;

import com.nearkart.dto.ShopDTO;
import com.nearkart.entity.Shop;
import com.nearkart.service.ShopService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    private final ShopService shopService;

    // =========================
    // CONSTRUCTOR
    // =========================

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    // =========================
    // CREATE SHOP
    // =========================

    @PostMapping
    public Shop createShop(@RequestBody Shop shop) {

        return shopService.createShop(shop);
    }

    // =========================
    // GET ALL SHOPS
    // =========================

    @GetMapping
    public List<ShopDTO> getAllShops() {

        return shopService.getAllShops();
    }

    // =========================
    // GET SHOP BY ID
    // =========================

    @GetMapping("/{id}")
    public ShopDTO getShopById(
            @PathVariable Long id) {

        return shopService.getShopById(id);
    }
    // =========================
    // UPDATE SHOP
    // =========================

    @PutMapping("/{id}")
    public Shop updateShop(
            @PathVariable Long id,
            @RequestBody Shop shop) {

        return shopService.updateShop(id, shop);
    }

    // =========================
    // DELETE SHOP
    // =========================

    @DeleteMapping("/{id}")
    public void deleteShop(
            @PathVariable Long id) {

        shopService.deleteShop(id);
    }
}