package com.example.demo.controller;

import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ContactController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/contact")
    public ResponseEntity<Map<String, String>> handleContactForm(@RequestBody ContactRequest request) {
        Map<String, String> response = new HashMap<>();

        try {
            // Validate required fields
            if (request.getSubject() == null || request.getSubject().trim().isEmpty()) {
                response.put("message", "Subject is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                response.put("message", "Message is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (request.getUserEmail() == null || request.getUserEmail().trim().isEmpty()) {
                response.put("message", "User email is required");
                return ResponseEntity.badRequest().body(response);
            }

            emailService.sendContactEmails(
                    request.getSubject(),
                    request.getMessage(),
                    request.getUserEmail()
            );

            response.put("message", "Message sent successfully! Check your email for confirmation.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to send message. Please try again.");
            return ResponseEntity.status(500).body(response);
        }
    }
}

// Updated DTO class
class ContactRequest {
    private String subject;
    private String message;
    private String userEmail;

    // Getters and setters
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}
