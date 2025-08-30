package com.example.demo.service;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.User;
import com.example.demo.repository.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public List<CartItem> getUserCart(User user) {
        return cartRepository.findByUser(user);
    }

    public CartItem addToCart(User user, CartItem cartItem) {
        // Check if already exists → update quantity instead
        return cartRepository.findByUser(user).stream()
                .filter(item -> item.getBookId().equals(cartItem.getBookId()))
                .findFirst()
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + cartItem.getQuantity());
                    return cartRepository.save(existing);
                })
                .orElseGet(() -> {
                    cartItem.setUser(user);
                    return cartRepository.save(cartItem);
                });
    }

    @Transactional
    public void removeFromCart(User user, String bookId) {
        cartRepository.deleteByUserAndBookId(user, bookId);
    }
}
