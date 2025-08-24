package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "your_books")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class YourBooks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(length = 255, nullable = false)
    private String author;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(length = 3, nullable = false)
    private String currency;

    @Column(nullable = false)
    private Integer quantity;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(name = "pickup_address", length = 500, nullable = false)
    private String pickupAddress;

    @Column(name = "orders_count", nullable = false)
    @Builder.Default
    private Integer ordersCount = 0;

    @Column(name = "created_date", nullable = false)
    @Builder.Default
    private LocalDateTime createdDate = LocalDateTime.now();

    // Relation: one Book belongs to one User (seller)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}