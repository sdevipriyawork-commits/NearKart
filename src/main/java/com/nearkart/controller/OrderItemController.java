package com.nearkart.controller;

import com.nearkart.entity.OrderItem;
import com.nearkart.service.OrderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    // POST - Create OrderItem
    @PostMapping
    public OrderItem createOrderItem(@RequestBody OrderItem orderItem) {
        return orderItemService.createOrderItem(orderItem);
    }

    // GET - Get All OrderItems
    @GetMapping
    public List<OrderItem> getAllOrderItems() {
        return orderItemService.getAllOrderItems();
    }

    // GET - Get Order Items By Order ID
    @GetMapping("/order/{orderId}")
    public List<OrderItem> getOrderItemsByOrderId(
            @PathVariable Long orderId) {

        return orderItemService.getOrderItemsByOrderId(orderId);
    }

    // GET - Get OrderItem By ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Long id) {

        return orderItemService.getOrderItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT - Update OrderItem
    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(
            @PathVariable Long id,
            @RequestBody OrderItem orderItemDetails) {

        return ResponseEntity.ok(
                orderItemService.updateOrderItem(id, orderItemDetails)
        );
    }

    // DELETE - Delete OrderItem
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {

        orderItemService.deleteOrderItem(id);

        return ResponseEntity.noContent().build();
    }
}