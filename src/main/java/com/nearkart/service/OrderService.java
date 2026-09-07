package com.nearkart.service;

import com.nearkart.dto.OrderDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nearkart.entity.Cart;
import com.nearkart.entity.CartItem;
import com.nearkart.entity.Order;
import com.nearkart.entity.OrderItem;
import com.nearkart.entity.Product;
import com.nearkart.entity.Shop;
import com.nearkart.entity.User;

import com.nearkart.repository.CartItemRepository;
import com.nearkart.repository.CartRepository;
import com.nearkart.repository.OrderItemRepository;
import com.nearkart.repository.OrderRepository;
import com.nearkart.repository.ProductRepository;
import com.nearkart.repository.ShopRepository;
import com.nearkart.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

    // =========================
    // CONSTRUCTOR
    // =========================

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            ShopRepository shopRepository,
            ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
    }

    // =========================
    // CREATE ORDER MANUALLY
    // =========================

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    // =========================
    // REAL PLACE ORDER
    // =========================

    @Transactional
    public Order placeOrder(
            Long userId,
            Long shopId,
            String deliveryAddress) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() ->
                        new RuntimeException("Shop not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // CHECK PRODUCT STOCK
        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            if (product.getStock() == null ||
                    product.getStock() < cartItem.getQuantity()) {

                throw new RuntimeException(
                        product.getProductName()
                                + " has insufficient stock"
                );
            }
        }

        // CALCULATE TOTAL AMOUNT
        double totalAmount = 0;

        for (CartItem cartItem : cartItems) {

            double itemTotal =
                    cartItem.getProduct().getPrice()
                            * cartItem.getQuantity();

            totalAmount += itemTotal;
        }

        // CREATE ORDER
        Order order = new Order();

        order.setUser(user);
        order.setShop(shop);
        order.setTotalAmount(totalAmount);
        order.setStatus("PLACED");
        order.setDeliveryAddress(deliveryAddress);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // CREATE ORDER ITEMS + REDUCE PRODUCT STOCK
        for (CartItem cartItem : cartItems) {

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(
                    cartItem.getProduct().getPrice()
            );

            orderItemRepository.save(orderItem);

            // REDUCE PRODUCT STOCK
            Product product = cartItem.getProduct();

            int newStock =
                    product.getStock() - cartItem.getQuantity();

            product.setStock(newStock);

            productRepository.save(product);
        }

        // CLEAR CART
        cartItemRepository.deleteAll(cartItems);

        return savedOrder;
    }

    // =========================
    // GET ALL ORDERS
    // =========================

    public List<OrderDTO> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // =========================
    // GET ORDERS BY USER ID
    // =========================

    public List<OrderDTO> getOrdersByUserId(Long userId) {

        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // =========================
    // GET ORDERS BY SHOP ID
    // =========================

    public List<OrderDTO> getOrdersByShopId(Long shopId) {

        return orderRepository.findByShopId(shopId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // =========================
    // GET ORDERS BY SHOP ID AND STATUS
    // =========================

    public List<OrderDTO> getOrdersByShopIdAndStatus(
            Long shopId,
            String status) {

        return orderRepository.findByShopIdAndStatus(
                        shopId,
                        status
                )
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // =========================
    // GET ORDER BY ID
    // =========================

    public OrderDTO getOrderById(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found with id: " + id
                        )
                );

        return convertToDTO(order);
    }

    // =========================
    // UPDATE ORDER
    // =========================

    public Order updateOrder(
            Long id,
            Order orderDetails) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        order.setTotalAmount(orderDetails.getTotalAmount());
        order.setStatus(orderDetails.getStatus());
        order.setDeliveryAddress(orderDetails.getDeliveryAddress());

        return orderRepository.save(order);
    }

    // =========================
    // UPDATE ORDER STATUS
    // =========================

    public Order updateOrderStatus(
            Long id,
            String status) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        order.setStatus(status);

        return orderRepository.save(order);
    }

    // =========================
    // CANCEL ORDER
    // =========================

    @Transactional
    public Order cancelOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if ("CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException("Order is already cancelled");
        }

        List<OrderItem> orderItems =
                orderItemRepository.findByOrderId(id);

        // RESTORE PRODUCT STOCK
        for (OrderItem orderItem : orderItems) {

            Product product = orderItem.getProduct();

            int restoredStock =
                    product.getStock() + orderItem.getQuantity();

            product.setStock(restoredStock);

            productRepository.save(product);
        }

        order.setStatus("CANCELLED");

        return orderRepository.save(order);
    }

    // =========================
    // DELETE ORDER
    // =========================

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    // =========================
    // CONVERT ORDER TO DTO
    // =========================

    public OrderDTO convertToDTO(Order order) {

        OrderDTO dto = new OrderDTO();

        dto.setId(order.getId());

        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
        }

        if (order.getShop() != null) {
            dto.setShopId(order.getShop().getId());
        }

        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setCreatedAt(order.getCreatedAt());

        return dto;
    }
}