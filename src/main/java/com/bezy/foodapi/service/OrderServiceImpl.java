package com.bezy.foodapi.service;

import com.bezy.foodapi.entity.OrderEntity;
import com.bezy.foodapi.io.OrderResponse;
import com.bezy.foodapi.repositories.CartRepository;
import com.bezy.foodapi.repositories.OrderRepository;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final String CLIENT_ID = "";
    private final String CLIENT_SECRET = "";
    private final String MODE = "sandbox"; // change to "live" for production
    private final CartRepository cartRepository;
    private UserService userService;

    private APIContext getApiContext() {
        return new APIContext(CLIENT_ID, CLIENT_SECRET, MODE);
    }

    @Override
    public OrderResponse createOrderWithPayment(OrderEntity request) {
        OrderEntity newOrder = convertToEntity(request);
        newOrder = orderRepository.save(newOrder);

        try {
            Amount amount = new Amount();
            amount.setCurrency("USD");
            amount.setTotal(String.format("%.2f", request.getAmount()));

            Transaction transaction = new Transaction();
            transaction.setDescription("Order Payment");
            transaction.setAmount(amount);
            transaction.setInvoiceNumber(String.valueOf(newOrder.getId()));

            List<Transaction> transactions = List.of(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            Payment payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl("http://localhost:5137/cancel");
            redirectUrls.setReturnUrl("http://localhost:5137/success");
            payment.setRedirectUrls(redirectUrls);

            Payment createdPayment = payment.create(getApiContext());

            String approvalUrl = createdPayment.getLinks().stream()
                    .filter(link -> "approval_url".equals(link.getRel()))
                    .map(Links::getHref)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No approval URL found"));

            return OrderResponse.builder()
                    .orderId(String.valueOf(newOrder.getId()))
                    .paymentApprovalUrl(approvalUrl)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating PayPal payment: " + e.getMessage());
        }
    }

    @Override
    public OrderResponse cancelPayment(String invoiceNumber) {
        if (invoiceNumber != null) {
            try {
                long orderId = Long.parseLong(invoiceNumber);
                Optional<OrderEntity> orderOpt = orderRepository.findById(String.valueOf(orderId));

                orderOpt.ifPresent(order -> {
                    order.setStatus("CANCELED");
                    orderRepository.save(order);
                });

                return OrderResponse.builder()
                        .paymentStatus("CANCELED")
                        .orderId(String.valueOf(orderId))
                        .build();

            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid invoice number: " + invoiceNumber);
            }
        }
        throw new RuntimeException("Invoice number is required to cancel payment");
    }

    @Override
    public void verifyPayment(Map<String, String> paymentData, String status) {
        String payPalOrderId = paymentData.get("paypal_order_id");
        OrderEntity existingOrder = orderRepository.findByPayPalOrderId(payPalOrderId)
                .orElseThrow(() -> new RuntimeException("PayPal order not found"));
        existingOrder.getPaymentStatus();
        existingOrder.setPayPalSignature(paymentData.get("paypal_signature"));
        existingOrder.setPayPalPaymentId(paymentData.get("paypal_payment_id"));
        orderRepository.save(existingOrder);
        if("paid".equalsIgnoreCase(status)) {
            cartRepository.deleteByUserId(existingOrder.getUserId());
        }
    }

    @Override
    public List<OrderEntity> getUserOrders() {
        String loggedUserId = userService.findByUserId();
        List<OrderEntity> list = orderRepository.findByUserId(loggedUserId);
        return list.stream().map(this::convertToEntity).collect(Collectors.toList());
    }

    @Override
    public void removeOrder(String orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public List<OrderEntity> getOrdersOfAllUsers() {
        List<OrderEntity> list = orderRepository.findAll();
        return list.stream().map(this::convertToEntity).collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        entity.setStatus(status);
        orderRepository.save(entity);
    }


    // Convert request to DB entity
    public OrderEntity convertToEntity(OrderEntity request) {
        return OrderEntity.builder()
                .userAddress(request.getUserAddress())
                .amount(request.getAmount())
                .orderItems(request.getOrderItems())
                .status("PENDING")
//                .orderedItems(request.getOrderedItems)// initial status
                .build();
    }
}
