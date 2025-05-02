package com.learnSpringBoot.eCom.services;

import com.learnSpringBoot.eCom.payload.CartDTO;
import org.springframework.stereotype.Service;


public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);
}
