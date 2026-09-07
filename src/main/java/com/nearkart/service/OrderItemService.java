package com.nearkart.service;

import com.nearkart.entity.OrderItem;
import com.nearkart.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    // Create OrderItem
    public OrderItem createOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    // Get All OrderItems
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }
    // Get Order Items By Order ID
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {

        return orderItemRepository.findByOrderId(orderId);
    }

    // Get OrderItem By ID
    public Optional<OrderItem> getOrderItemById(Long id) {
        return orderItemRepository.findById(id);
    }


    // Update OrderItem
    public OrderItem updateOrderItem(Long id, OrderItem orderItemDetails) {

        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));

        orderItem.setQuantity(orderItemDetails.getQuantity());
        orderItem.setPrice(orderItemDetails.getPrice());

        return orderItemRepository.save(orderItem);
    }

    // Delete OrderItem
    public void deleteOrderItem(Long id) {
        orderItemRepository.deleteById(id);
    }
}