package com.nearkart.service;

import com.nearkart.entity.Cart;
import com.nearkart.entity.CartItem;
import com.nearkart.entity.Product;
import com.nearkart.entity.User;
import com.nearkart.repository.CartItemRepository;
import com.nearkart.repository.CartRepository;
import com.nearkart.repository.ProductRepository;
import com.nearkart.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartItemService(
            CartItemRepository cartItemRepository,
            CartRepository cartRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // Create CartItem manually
    public CartItem createCartItem(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    // Real Add Product to Cart
    public CartItem addToCart(Long userId, Long productId, Integer quantity) {

        // Find User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find Product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Find Cart
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Check if product already exists in cart
        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElse(null);

        if (cartItem != null) {

            // Product already exists → increase quantity
            cartItem.setQuantity(cartItem.getQuantity() + quantity);

        } else {

            // Product does not exist → create new CartItem
            cartItem = new CartItem();

            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }

        return cartItemRepository.save(cartItem);
    }

    // Get Cart Items by User
    public List<CartItem> getCartItemsByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        return cartItemRepository.findByCart(cart);
    }

    // Get All Cart Items
    public List<CartItem> getAllCartItems() {
        return cartItemRepository.findAll();
    }

    // Get Cart Item By ID
    public CartItem getCartItemById(Long id) {
        return cartItemRepository.findById(id).orElse(null);
    }
    // Update Cart Item Quantity
    public CartItem updateQuantity(Long id, Integer quantity) {

        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartItem.setQuantity(quantity);

        return cartItemRepository.save(cartItem);
    }
    // Clear Cart for a User
    public void clearCart(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        cartItemRepository.deleteAll(cartItems);
    }

    // Delete Cart Item
    public void deleteCartItem(Long id) {
        cartItemRepository.deleteById(id);
    }
}