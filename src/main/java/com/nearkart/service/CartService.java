package com.nearkart.service;

import com.nearkart.dto.CartDTO;
import com.nearkart.dto.CartItemDTO;
import com.nearkart.entity.Cart;
import com.nearkart.entity.CartItem;
import com.nearkart.entity.Product;
import com.nearkart.entity.User;
import com.nearkart.exception.ResourceNotFoundException;
import com.nearkart.repository.CartItemRepository;
import com.nearkart.repository.CartRepository;
import com.nearkart.repository.ProductRepository;
import com.nearkart.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    // =========================
    // CONSTRUCTOR
    // =========================

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }


    // =========================
    // CREATE CART
    // =========================

    public Cart createCart(Cart cart) {

        cart.setCreatedAt(LocalDateTime.now());

        return cartRepository.save(cart);
    }


    // =========================
    // ADD PRODUCT TO CART
    // =========================

    public CartItem addToCart(
            Long userId,
            Long productId,
            Integer quantity) {

        // FIND USER
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        )
                );


        // FIND PRODUCT
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + productId
                        )
                );


        // FIND CART OR CREATE NEW CART
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {

                    Cart newCart = new Cart();

                    newCart.setUser(user);
                    newCart.setCreatedAt(LocalDateTime.now());

                    return cartRepository.save(newCart);
                });


        // CHECK IF PRODUCT ALREADY EXISTS IN CART
        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElse(null);


        if (cartItem != null) {

            // INCREASE QUANTITY
            cartItem.setQuantity(
                    cartItem.getQuantity() + quantity
            );

        } else {

            // CREATE NEW CART ITEM
            cartItem = new CartItem();

            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }


        return cartItemRepository.save(cartItem);
    }


    // =========================
    // GET ALL CARTS
    // =========================

    public List<CartDTO> getAllCarts() {

        return cartRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    // =========================
    // GET CART BY ID
    // =========================

    public CartDTO getCartById(Long id) {

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart not found with id: " + id
                        )
                );

        return convertToDTO(cart);
    }


    // =========================
    // UPDATE CART ITEM QUANTITY
    // =========================

    public CartItem updateQuantity(
            Long cartItemId,
            Integer quantity) {

        CartItem cartItem = cartItemRepository
                .findById(cartItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item not found with id: "
                                        + cartItemId
                        )
                );


        cartItem.setQuantity(quantity);

        return cartItemRepository.save(cartItem);
    }


    // =========================
    // REMOVE CART ITEM
    // =========================

    public void removeCartItem(Long cartItemId) {

        CartItem cartItem = cartItemRepository
                .findById(cartItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item not found with id: "
                                        + cartItemId
                        )
                );


        cartItemRepository.delete(cartItem);
    }


    // =========================
    // DELETE CART
    // =========================

    public void deleteCart(Long id) {

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart not found with id: " + id
                        )
                );


        cartRepository.delete(cart);
    }


    // =========================
    // CONVERT CART TO DTO
    // =========================

    private CartDTO convertToDTO(Cart cart) {

        CartDTO dto = new CartDTO();

        dto.setId(cart.getId());


        // USER ID
        if (cart.getUser() != null) {

            dto.setUserId(
                    cart.getUser().getId()
            );
        }


        dto.setCreatedAt(cart.getCreatedAt());


        // =========================
        // GET CART ITEMS
        // =========================

        List<CartItemDTO> items =
                cartItemRepository.findByCart(cart)
                        .stream()
                        .map(this::convertCartItemToDTO)
                        .toList();


        dto.setItems(items);

        return dto;
    }


    // =========================
    // CONVERT CART ITEM TO DTO
    // =========================

    private CartItemDTO convertCartItemToDTO(
            CartItem cartItem) {

        CartItemDTO dto = new CartItemDTO();


        dto.setId(cartItem.getId());


        Product product = cartItem.getProduct();


        if (product != null) {

            dto.setProductId(product.getId());
            dto.setProductName(product.getProductName());
            dto.setPrice(product.getPrice());
            dto.setImageUrl(product.getImageUrl());


            // CALCULATE SUBTOTAL
            if (product.getPrice() != null &&
                    cartItem.getQuantity() != null) {

                dto.setSubtotal(
                        product.getPrice()
                                * cartItem.getQuantity()
                );
            }
        }


        dto.setQuantity(cartItem.getQuantity());

        return dto;
    }
}