package com.bezy.foodapi.io;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderResponse {
    private String id;
    private String userId;
    private String orderId;
    private String paymentApprovalUrl;
    private String userAddress;
    private String phoneNumber;
    private String email;
    private double amount;
    private String paymentStatus;
    private String paypalOrderId;
    private String paypalSignature;
    private String orderStatus;
    private List<OrderItem> orderItems;
}
