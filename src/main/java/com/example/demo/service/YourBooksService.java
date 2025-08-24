package com.example.demo.service;


import com.example.demo.dto.*;
import com.example.demo.entity.YourBooks;
import com.example.demo.entity.User;
import com.example.demo.repository.YourBooksRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class YourBooksService {

    private final YourBooksRepository yourBooksRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public YourBooksService(YourBooksRepository yourBooksRepository) {
        this.yourBooksRepository = yourBooksRepository;
    }

    public List<YourBooksResponse> getAllBooksByUser(User user) {
        return yourBooksRepository.findByUserOrderByCreatedDateDesc(user)
                .stream()
                .map(this::convertToYourBooksResponse)
                .collect(Collectors.toList());
    }

    public Page<YourBooksResponse> getAllBooksByUser(User user, Pageable pageable) {
        return yourBooksRepository.findByUserOrderByCreatedDateDesc(user, pageable)
                .map(this::convertToYourBooksResponse);
    }

    public Optional<YourBooksResponse> getBookByIdAndUser(Long bookId, User user) {
        return yourBooksRepository.findByIdAndUser(bookId, user)
                .map(this::convertToYourBooksResponse);
    }

    public YourBooksResponse createBook(YourBooksRequest request, User user) {
        YourBooks book = YourBooks.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .price(request.getPrice())
                .currency(request.getCurrency())
                .quantity(request.getQuantity())
                .description(request.getDescription())
                .coverImageUrl(request.getCoverImageUrl())
                .pickupAddress(request.getPickupAddress())
                .user(user)
                .build();

        YourBooks savedBook = yourBooksRepository.save(book);
        return convertToYourBooksResponse(savedBook);
    }

    public YourBooksResponse updateBook(Long bookId, YourBooksRequest request, User user) {
        YourBooks book = yourBooksRepository.findByIdAndUser(bookId, user)
                .orElseThrow(() -> new RuntimeException("Book not found or doesn't belong to user"));

        // Update fields only if they are provided
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            book.setTitle(request.getTitle());
        }
        if (request.getAuthor() != null && !request.getAuthor().isBlank()) {
            book.setAuthor(request.getAuthor());
        }
        if (request.getPrice() != null) {
            book.setPrice(request.getPrice());
        }
        if (request.getCurrency() != null && !request.getCurrency().isBlank()) {
            book.setCurrency(request.getCurrency());
        }
        if (request.getQuantity() != null) {
            book.setQuantity(request.getQuantity());
        }
        if (request.getDescription() != null) {
            book.setDescription(request.getDescription());
        }
        if (request.getCoverImageUrl() != null && !request.getCoverImageUrl().isBlank()) {
            book.setCoverImageUrl(request.getCoverImageUrl());
        }
        if (request.getPickupAddress() != null && !request.getPickupAddress().isBlank()) {
            book.setPickupAddress(request.getPickupAddress());
        }

        YourBooks savedBook = yourBooksRepository.save(book);
        return convertToYourBooksResponse(savedBook);
    }

    public void deleteBook(Long bookId, User user) {
        YourBooks book = yourBooksRepository.findByIdAndUser(bookId, user)
                .orElseThrow(() -> new RuntimeException("Book not found or doesn't belong to user"));

        yourBooksRepository.delete(book);
    }

    public List<YourBooksResponse> searchBooks(String searchTerm, User user) {
        return yourBooksRepository.findByUserAndTitleOrAuthorContainingIgnoreCase(user, searchTerm)
                .stream()
                .map(this::convertToYourBooksResponse)
                .collect(Collectors.toList());
    }

    public long getTotalBooksByUser(User user) {
        return yourBooksRepository.countByUser(user);
    }

    private YourBooksResponse convertToYourBooksResponse(YourBooks book) {
        return YourBooksResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .price(book.getPrice())
                .currency(book.getCurrency())
                .quantity(book.getQuantity())
                .description(book.getDescription())
                .coverImageUrl(book.getCoverImageUrl())
                .pickupAddress(book.getPickupAddress())
                .ordersCount(book.getOrdersCount())
                .createdDate(book.getCreatedDate().format(dateFormatter))
                .build();
    }
}
