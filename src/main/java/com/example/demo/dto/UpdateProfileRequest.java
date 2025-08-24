package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String profilePictureUrl; // URL (you can later replace with file upload)
}
