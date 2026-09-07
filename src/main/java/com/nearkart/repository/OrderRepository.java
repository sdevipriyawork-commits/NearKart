package com.nearkart.repository;

import com.nearkart.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Get all orders for a specific user
    List<Order> findByUserId(Long userId);

    // Get all orders for a specific shop
    List<Order> findByShopId(Long shopId);

    // Get orders by Shop ID and Status
    List<Order> findByShopIdAndStatus(Long shopId, String status);
}