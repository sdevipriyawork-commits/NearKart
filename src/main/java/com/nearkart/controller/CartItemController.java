package com.nearkart.controller;

import com.nearkart.entity.CartItem;
import com.nearkart.service.CartItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    // Add Product to Cart
    @PostMapping("/add")
    public CartItem addToCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        return cartItemService.addToCart(userId, productId, quantity);
    }

    // Get All Cart Items
    @GetMapping
    public List<CartItem> getAllCartItems() {
        return cartItemService.getAllCartItems();
    }

    // Get Cart Items for Specific User
    @GetMapping("/user/{userId}")
    public List<CartItem> getCartItemsByUser(
            @PathVariable Long userId) {

        return cartItemService.getCartItemsByUser(userId);
    }

    // Get Cart Item By ID
    @GetMapping("/{id}")
    public CartItem getCartItemById(@PathVariable Long id) {
        return cartItemService.getCartItemById(id);
    }
    // Update Cart Item Quantity
    @PutMapping("/{id}/quantity")
    public CartItem updateQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantity) {

        return cartItemService.updateQuantity(id, quantity);
    }
    // Clear Entire Cart for User
    @DeleteMapping("/user/{userId}")
    public void clearCart(@PathVariable Long userId) {

        cartItemService.clearCart(userId);
    }


    // Delete Cart Item
    @DeleteMapping("/{id}")
    public void deleteCartItem(@PathVariable Long id) {
        cartItemService.deleteCartItem(id);
    }
}