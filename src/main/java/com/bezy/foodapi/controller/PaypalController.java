package com.bezy.foodapi.controller;

import com.bezy.foodapi.entity.OrderEntity;
import com.bezy.foodapi.io.OrderRequest;
import com.bezy.foodapi.io.OrderResponse;
import com.bezy.foodapi.service.OrderService;
import com.bezy.foodapi.service.OrderServiceImpl;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class PaypalController {
    private OrderService orderService;


    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createPayment(
            @RequestBody OrderEntity request
    ) throws PayPalRESTException {

        return orderService.createOrderWithPayment(request);
    }

    @PostMapping("/verify")
    public void verifyPayment(
            @RequestBody Map<String, String> paymentData
    ) {
        orderService.verifyPayment(paymentData, "Paid");
    }

    @GetMapping
    public List<?> getOrders() {
        return orderService.getUserOrders();
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(
            @PathVariable String orderId
    ){
        orderService.removeOrder(orderId);
    }

    @GetMapping("/all")
    public List<?> getOrdersOfAllUsers() {
        return orderService.getOrdersOfAllUsers();
    }

    @PatchMapping("/status/{orderId}")
    public void updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam String status
    ) {
        orderService.updateOrderStatus(orderId, status);
    }

}
