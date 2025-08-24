package com.example.demo.dto;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class YourBooksResponse {
    private Long id;
    private String title;
    private String author;
    private BigDecimal price;
    private String currency;
    private Integer quantity;
    private String description;
    private String coverImageUrl;
    private String pickupAddress;
    private Integer ordersCount;
    private String createdDate;
}
