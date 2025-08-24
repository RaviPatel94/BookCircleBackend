package com.example.demo.controller;

import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    public ProfileController(UserService userService, CloudinaryService cloudinaryService) {
        this.userService = userService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping
    public ResponseEntity<User> getProfile(Authentication authentication) {
        String email = authentication.getName();

        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PutMapping
    public ResponseEntity<User> updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request
    ) {
        String email = authentication.getName();

        User updatedUser = userService.findByEmail(email)
                .map(user -> {
                    user.setFullName(request.getFullName());
                    user.setPhoneNumber(request.getPhoneNumber());
                    user.setDateOfBirth(request.getDateOfBirth());

                    // ✅ only update if a new value is provided
                    if (request.getProfilePictureUrl() != null && !request.getProfilePictureUrl().isBlank()) {
                        user.setProfilePic(request.getProfilePictureUrl());
                    }

                    return userService.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(updatedUser);
    }


    @PostMapping("/upload-picture")
    public ResponseEntity<?> uploadProfilePic(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        String email = authentication.getName();

        return userService.findByEmail(email)
                .map(user -> {
                    try {
                        // Upload file to Cloudinary
                        String imageUrl = cloudinaryService.uploadFile(file);

                        // Save URL in user profile
                        user.setProfilePic(imageUrl);  // ✅ use profilePic
                        userService.save(user);

                        return ResponseEntity.ok(Map.of("profilePic", imageUrl)); // ✅ return profilePic
                    } catch (Exception e) {
                        return ResponseEntity.internalServerError()
                                .body(Map.of("error", e.getMessage()));
                    }
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
