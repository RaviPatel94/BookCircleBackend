package com.example.demo.controller;

import com.example.demo.dto.AddressDto;
import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address")
public class AddressController {

    private final UserService userService;

    public AddressController(UserService userService) {
        this.userService = userService;
    }

    // Get logged-in user's address
    @GetMapping
    public ResponseEntity<Address> getAddress(Authentication authentication) {
        String email = authentication.getName();

        return userService.findByEmail(email)
                .map(user -> ResponseEntity.ok(user.getAddress()))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Save or update address
    @PutMapping
    public ResponseEntity<Address> saveOrUpdateAddress(
            Authentication authentication,
            @RequestBody AddressDto request
    ) {
        String email = authentication.getName();

        User updatedUser = userService.findByEmail(email)
                .map(user -> {
                    Address address = user.getAddress();
                    if (address == null) {
                        address = new Address();
                        address.setUser(user);
                    }

                    address.setAddressLine1(request.addressLine1());
                    address.setAddressLine2(request.addressLine2());
                    address.setCity(request.city());
                    address.setPostalCode(request.postalCode());
                    address.setCountry(request.country());

                    user.setAddress(address);
                    return userService.save(user); // cascade saves address too
                })
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(updatedUser.getAddress());
    }
}
