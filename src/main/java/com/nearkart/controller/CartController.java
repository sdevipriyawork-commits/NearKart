package com.nearkart.controller;

import com.nearkart.dto.CartDTO;
import com.nearkart.entity.CartItem;
import com.nearkart.service.CartService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;


    // =========================
    // CONSTRUCTOR
    // =========================

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    // =========================
    // ADD PRODUCT TO LOGGED-IN USER CART
    // =========================

    @PostMapping("/add")
    public CartItem addToCart(

            @RequestParam Long productId,

            @RequestParam Integer quantity) {

        return cartService.addToCart(
                productId,
                quantity
        );
    }


    // =========================
    // GET LOGGED-IN USER CART
    // =========================

    @GetMapping
    public CartDTO getMyCart() {

        return cartService.getMyCart();
    }


    // =========================
    // UPDATE CART ITEM QUANTITY
    // =========================

    @PutMapping("/item/{cartItemId}")
    public CartItem updateQuantity(

            @PathVariable Long cartItemId,

            @RequestParam Integer quantity) {

        return cartService.updateQuantity(
                cartItemId,
                quantity
        );
    }


    // =========================
    // REMOVE CART ITEM
    // =========================

    @DeleteMapping("/item/{cartItemId}")
    public void removeCartItem(

            @PathVariable Long cartItemId) {

        cartService.removeCartItem(cartItemId);
    }


    // =========================
    // DELETE LOGGED-IN USER CART
    // =========================

    @DeleteMapping
    public void deleteMyCart() {

        cartService.deleteMyCart();
    }
}