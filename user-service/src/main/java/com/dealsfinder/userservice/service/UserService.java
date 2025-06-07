package com.dealsfinder.userservice.service;

import com.dealsfinder.userservice.model.Deal;
import com.dealsfinder.userservice.model.User;
import com.dealsfinder.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${deal.service.url}")
    private String dealServiceUrl;

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email is already in use.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash password
        return userRepository.save(user);
    }

    public List<Deal> getAllDeals(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Deal[]> response = restTemplate.exchange(
                    dealServiceUrl + "/deals/all",
                    HttpMethod.GET,
                    entity,
                    Deal[].class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return List.of(response.getBody());
            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            System.out.println("Deal service is unavailable. Returning empty deal list.");
            return Collections.emptyList();
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
