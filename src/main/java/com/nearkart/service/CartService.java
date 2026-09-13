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

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;


    // =========================
    // CONSTRUCTOR
    // =========================

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }


    // =========================
    // GET CURRENT LOGGED-IN USER
    // =========================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof User)) {

            throw new AccessDeniedException(
                    "User is not authenticated"
            );
        }


        return (User) authentication.getPrincipal();
    }


    // =========================
    // ADD PRODUCT TO CART
    // =========================

    public CartItem addToCart(
            Long productId,
            Integer quantity) {

        // VALIDATE QUANTITY
        if (quantity == null || quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than 0"
            );
        }


        // GET LOGGED-IN USER
        User user = getCurrentUser();


        // FIND PRODUCT
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: "
                                        + productId
                        )
                );


        // FIND USER CART OR CREATE NEW CART
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {

                    Cart newCart = new Cart();

                    newCart.setUser(user);
                    newCart.setCreatedAt(LocalDateTime.now());

                    return cartRepository.save(newCart);
                });


        // CHECK IF PRODUCT ALREADY EXISTS
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
    // GET LOGGED-IN USER CART
    // =========================

    public CartDTO getMyCart() {

        User user = getCurrentUser();


        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {

                    Cart newCart = new Cart();

                    newCart.setUser(user);
                    newCart.setCreatedAt(LocalDateTime.now());

                    return cartRepository.save(newCart);
                });


        return convertToDTO(cart);
    }


    // =========================
    // UPDATE CART ITEM QUANTITY
    // =========================

    public CartItem updateQuantity(
            Long cartItemId,
            Integer quantity) {

        // VALIDATE QUANTITY
        if (quantity == null || quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than 0"
            );
        }


        // GET CURRENT USER
        User currentUser = getCurrentUser();


        // FIND CART ITEM
        CartItem cartItem = cartItemRepository
                .findById(cartItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item not found with id: "
                                        + cartItemId
                        )
                );


        // =========================
        // OWNERSHIP CHECK
        // =========================

        if (!cartItem.getCart()
                .getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "You are not allowed to update this cart item"
            );
        }


        cartItem.setQuantity(quantity);

        return cartItemRepository.save(cartItem);
    }


    // =========================
    // REMOVE CART ITEM
    // =========================

    public void removeCartItem(Long cartItemId) {

        User currentUser = getCurrentUser();


        CartItem cartItem = cartItemRepository
                .findById(cartItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item not found with id: "
                                        + cartItemId
                        )
                );


        // =========================
        // OWNERSHIP CHECK
        // =========================

        if (!cartItem.getCart()
                .getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "You are not allowed to delete this cart item"
            );
        }


        cartItemRepository.delete(cartItem);
    }


    // =========================
    // DELETE LOGGED-IN USER CART
    // =========================

    public void deleteMyCart() {

        User currentUser = getCurrentUser();


        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart not found"
                        )
                );


        // DELETE CART ITEMS FIRST
        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        cartItemRepository.deleteAll(cartItems);


        // DELETE CART
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


        // GET CART ITEMS
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

            dto.setProductName(
                    product.getProductName()
            );

            dto.setPrice(product.getPrice());

            dto.setImageUrl(
                    product.getImageUrl()
            );


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