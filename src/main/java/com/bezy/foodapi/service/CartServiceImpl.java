package com.bezy.foodapi.service;

import com.bezy.foodapi.entity.CartEntity;
import com.bezy.foodapi.io.CartRequest;
import com.bezy.foodapi.io.CartResponse;
import com.bezy.foodapi.repositories.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final UserService userService;

    @Override
    public CartResponse addToCart(CartRequest request) {
        String loggedInUserId = userService.findByUserId();
        Optional<CartEntity> cartOptional = cartRepository.findByUserId(loggedInUserId);
        CartEntity cart = cartOptional.orElseGet(() -> new CartEntity(loggedInUserId, new HashMap<>()));
        Map<String, Integer> cartItems =  cart.getItems();
        cartItems.put(request.getFoodId(), cartItems.getOrDefault(request.getFoodId(),0) + 1);
        cart.setItems(cartItems);
        cart = cartRepository.save(cart);
        return convertToResponse(cart);
    }

    @Override
    public CartResponse getCart() {
        String loggedInUser = userService.findByUserId();
        CartEntity entity = cartRepository.findByUserId(loggedInUser)
                .orElse(new CartEntity(null, loggedInUser, new HashMap<>()));
        return convertToResponse(entity);
    }

    @Override
    public void clearCart() {
        String  loggedInUser = userService.findByUserId();
        cartRepository.deleteByUserId(loggedInUser);
    }

    @Override
    public CartResponse removeFromCart(CartRequest request) {
        String loggedInUser = userService.findByUserId();
        CartEntity entity = cartRepository.findByUserId(loggedInUser)
                .orElseThrow(() -> new RuntimeException("Cart is not present!"));
        Map<String, Integer> cartItems = entity.getItems();
        if (cartItems.containsKey(request.getFoodId())) {
            int currentQty = cartItems.get(request.getFoodId());
            if (currentQty > 0) {
                cartItems.put(request.getFoodId(), --currentQty);
            } else {
                cartItems.remove(request.getFoodId());
            }
            entity = cartRepository.save(entity);
        }
        return convertToResponse(entity);
    }

    private CartResponse convertToResponse(CartEntity cartEntity) {
        return CartResponse.builder()
                .id(cartEntity.getId())
                .userId(cartEntity.getUserId())
                .items(cartEntity.getItems())
                .build();
    }
}
