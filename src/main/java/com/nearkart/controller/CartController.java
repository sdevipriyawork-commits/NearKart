package com.nearkart.controller;

import com.nearkart.dto.CartDTO;
import com.nearkart.entity.Cart;
import com.nearkart.entity.CartItem;
import com.nearkart.service.CartService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    // CREATE CART
    // =========================

    @PostMapping
    public Cart createCart(@RequestBody Cart cart) {

        return cartService.createCart(cart);
    }


    // =========================
    // ADD PRODUCT TO CART
    // =========================

    @PostMapping("/add")
    public CartItem addToCart(

            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        return cartService.addToCart(
                userId,
                productId,
                quantity
        );
    }


    // =========================
    // GET ALL CARTS
    // =========================

    @GetMapping
    public List<CartDTO> getAllCarts() {

        return cartService.getAllCarts();
    }


    // =========================
    // GET CART BY ID
    // =========================

    @GetMapping("/{id}")
    public CartDTO getCartById(
            @PathVariable Long id) {

        return cartService.getCartById(id);
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
    // DELETE CART
    // =========================

    @DeleteMapping("/{id}")
    public void deleteCart(
            @PathVariable Long id) {

        cartService.deleteCart(id);
    }
}