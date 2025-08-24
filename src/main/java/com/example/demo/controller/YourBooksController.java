package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.User;
import com.example.demo.service.YourBooksService;
import com.example.demo.service.UserService;
import com.example.demo.service.CloudinaryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/your-books")
@CrossOrigin(origins = "*")
public class YourBooksController {

    private final YourBooksService yourBooksService;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    public YourBooksController(YourBooksService yourBooksService, UserService userService, CloudinaryService cloudinaryService) {
        this.yourBooksService = yourBooksService;
        this.userService = userService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping
    public ResponseEntity<List<YourBooksResponse>> getAllBooks(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<YourBooksResponse> books = yourBooksService.getAllBooksByUser(user);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<YourBooksResponse>> getAllBooksPaginated(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<YourBooksResponse> books = yourBooksService.getAllBooksByUser(user, pageable);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<YourBooksResponse> getBook(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return yourBooksService.getBookByIdAndUser(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<YourBooksResponse> createBook(
            @RequestBody YourBooksRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            // Validation
            if (request.getTitle() == null || request.getTitle().isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            if (request.getAuthor() == null || request.getAuthor().isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            if (request.getPrice() == null || request.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().build();
            }
            if (request.getQuantity() == null || request.getQuantity() <= 0) {
                return ResponseEntity.badRequest().build();
            }
            if (request.getPickupAddress() == null || request.getPickupAddress().isBlank()) {
                return ResponseEntity.badRequest().build();
            }

            // Set default values if not provided
            if (request.getCurrency() == null || request.getCurrency().isBlank()) {
                request.setCurrency("₹");
            }
            if (request.getCoverImageUrl() == null || request.getCoverImageUrl().isBlank()) {
                request.setCoverImageUrl("/images/bookcover.png");
            }

            YourBooksResponse createdBook = yourBooksService.createBook(request, user);
            return ResponseEntity.ok(createdBook);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<YourBooksResponse> updateBook(
            @PathVariable Long id,
            @RequestBody YourBooksRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            YourBooksResponse updatedBook = yourBooksService.updateBook(id, request, user);
            return ResponseEntity.ok(updatedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBook(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            yourBooksService.deleteBook(id, user);
            return ResponseEntity.ok(Map.of("message", "Book deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<YourBooksResponse>> searchBooks(
            @RequestParam String query,
            Authentication authentication
    ) {
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<YourBooksResponse> books = yourBooksService.searchBooks(query, user);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getBookStats(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long totalBooks = yourBooksService.getTotalBooksByUser(user);
        return ResponseEntity.ok(Map.of(
                "totalBooks", totalBooks
        ));
    }

    @PostMapping("/upload-cover")
    public ResponseEntity<Map<String, String>> uploadBookCover(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        String email = authentication.getName();
        userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Please select a file to upload"));
            }

            // Check file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Please upload a valid image file"));
            }

            String imageUrl = cloudinaryService.uploadFile(file);
            return ResponseEntity.ok(Map.of("coverImageUrl", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        }
    }
}