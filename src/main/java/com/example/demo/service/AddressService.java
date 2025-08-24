package com.example.demo.service;

import com.example.demo.dto.AddressDto;
import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import com.example.demo.repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressService {

    private final AddressRepository addressRepo;

    public AddressService(AddressRepository addressRepo) {
        this.addressRepo = addressRepo;
    }

    @Transactional(readOnly = true)
    public Address getFor(User user) {
        return addressRepo.findByUserId(user.getId()).orElse(null);
    }

    @Transactional
    public Address upsertFor(User user, AddressDto req) {
        Address address = addressRepo.findByUserId(user.getId()).orElseGet(() -> {
            Address a = new Address();
            a.setUser(user);
            return a;
        });

        address.setAddressLine1(req.addressLine1());
        address.setAddressLine2(req.addressLine2());
        address.setCity(req.city());
        address.setPostalCode(req.postalCode());
        address.setCountry(req.country());

        // Important: keep the reverse link consistent
        if (user.getAddress() == null) {
            user.setAddress(address);
        }

        return addressRepo.save(address);
    }
}

