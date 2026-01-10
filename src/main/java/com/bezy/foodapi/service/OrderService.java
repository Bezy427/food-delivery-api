package com.bezy.foodapi.service;

import com.bezy.foodapi.entity.OrderEntity;
import com.bezy.foodapi.io.OrderResponse;

import java.util.List;
import java.util.Map;

public interface OrderService {
    OrderResponse createOrderWithPayment(OrderEntity request);

    OrderResponse cancelPayment(String invoiceNumber);

    void verifyPayment(Map<String, String> paymentData, String status);

    List<OrderEntity> getUserOrders();


    void removeOrder(String orderId);

    List<OrderEntity> getOrdersOfAllUsers();

    void updateOrderStatus(String orderId, String status);
}
