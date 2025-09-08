package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendContactEmails(String subject, String message, String userEmail) {
        try {
            sendAdminNotification(subject, message, userEmail);
            sendUserConfirmation(subject, userEmail);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private void sendAdminNotification(String subject, String message, String userEmail) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo("ravi404err@gmail.com");
            email.setSubject("New Contact Form: " + subject);

            String emailBody = "You have received a new contact form submission:\n\n" +
                    "Subject: " + subject + "\n" +
                    "From: " + userEmail + "\n" +
                    "Message: " + message + "\n\n" +
                    "Please respond to: " + userEmail;

            email.setText(emailBody);
            email.setFrom("ravi404err@gmail.com");
            email.setReplyTo(userEmail);

            mailSender.send(email);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send admin notification email: " + e.getMessage(), e);
        }
    }

    private void sendUserConfirmation(String subject, String userEmail) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(userEmail);
            email.setSubject("We've received your message - " + subject);

            String confirmationBody = "Dear User,\n\n" +
                    "Thank you for contacting BookCircle!\n\n" +
                    "We have successfully received your message regarding: " + subject + "\n\n" +
                    "Our team will review your message and get back to you within 24-48 hours.\n\n" +
                    "If you have any urgent concerns, please feel free to call us at +91 888888888.\n\n" +
                    "Best regards,\n" +
                    "The BookCircle Team\n" +
                    "bookcircle@gmail.com";

            email.setText(confirmationBody);
            email.setFrom("ravi404err@gmail.com");

            mailSender.send(email);

        } catch (Exception e) {
            System.err.println("Failed to send confirmation email: " + e.getMessage());
        }
    }
}