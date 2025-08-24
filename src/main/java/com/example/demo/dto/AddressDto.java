package com.example.demo.dto;

public record AddressDto(
        String addressLine1,
        String addressLine2,
        String city,
        String postalCode,
        String country
) {}
