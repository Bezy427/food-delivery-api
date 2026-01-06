package com.bezy.foodapi.service;

import com.bezy.foodapi.io.CartRequest;
import com.bezy.foodapi.io.CartResponse;

public interface CartService {

    CartResponse addToCart(CartRequest request);

    CartResponse getCart();

    void clearCart();

    CartResponse removeFromCart(CartRequest request);
}
