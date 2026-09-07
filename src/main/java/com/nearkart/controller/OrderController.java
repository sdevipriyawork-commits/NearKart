package com.nearkart.controller;

import com.nearkart.dto.OrderDTO;
import com.nearkart.entity.Order;
import com.nearkart.service.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // =========================
    // CONSTRUCTOR
    // =========================

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // =========================
    // POST - CREATE ORDER
    // =========================

    @PostMapping
    public Order createOrder(@RequestBody Order order) {

        return orderService.createOrder(order);
    }

    // =========================
    // POST - REAL PLACE ORDER
    // =========================

    @PostMapping("/place")
    public Order placeOrder(
            @RequestParam Long userId,
            @RequestParam Long shopId,
            @RequestParam String deliveryAddress) {

        return orderService.placeOrder(
                userId,
                shopId,
                deliveryAddress
        );
    }

    // =========================
    // GET - ALL ORDERS
    // =========================

    @GetMapping
    public List<OrderDTO> getAllOrders() {

        return orderService.getAllOrders();
    }

    // =========================
    // GET - ORDERS BY USER ID
    // =========================

    @GetMapping("/user/{userId}")
    public List<OrderDTO> getOrdersByUserId(
            @PathVariable Long userId) {

        return orderService.getOrdersByUserId(userId);
    }

    // =========================
    // GET - ORDERS BY SHOP ID
    // =========================

    @GetMapping("/shop/{shopId}")
    public List<OrderDTO> getOrdersByShopId(
            @PathVariable Long shopId) {

        return orderService.getOrdersByShopId(shopId);
    }

    // =========================
    // GET - ORDERS BY SHOP ID AND STATUS
    // =========================

    @GetMapping("/shop/{shopId}/status")
    public List<OrderDTO> getOrdersByShopIdAndStatus(
            @PathVariable Long shopId,
            @RequestParam String status) {

        return orderService.getOrdersByShopIdAndStatus(
                shopId,
                status
        );
    }

    // =========================
    // GET - ORDER BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.getOrderById(id)
        );
    }

    // =========================
    // PUT - UPDATE ORDER
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(
            @PathVariable Long id,
            @RequestBody Order orderDetails) {

        return ResponseEntity.ok(
                orderService.updateOrder(id, orderDetails)
        );
    }

    // =========================
    // PUT - UPDATE ORDER STATUS
    // =========================

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                orderService.updateOrderStatus(id, status)
        );
    }

    // =========================
    // PUT - CANCEL ORDER
    // =========================

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.cancelOrder(id)
        );
    }

    // =========================
    // DELETE - DELETE ORDER
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable Long id) {

        orderService.deleteOrder(id);

        return ResponseEntity.noContent().build();
    }
}