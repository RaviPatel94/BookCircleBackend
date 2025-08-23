package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ProfileController {

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        // Authentication is automatically injected by Spring Security
        String email = authentication.getName(); // the email/username from JWT

        return ResponseEntity.ok(Map.of(
                "message", "Profile fetched successfully",
                "email", email
        ));
    }
}

