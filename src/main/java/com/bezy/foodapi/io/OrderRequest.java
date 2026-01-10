package com.bezy.foodapi.io;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderRequest {
    private String userId;
    private List<OrderItem> orderItems;
    private String userAddress;
    private double amount;
}
