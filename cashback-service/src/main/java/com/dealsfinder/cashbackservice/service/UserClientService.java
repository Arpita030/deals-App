package com.dealsfinder.cashbackservice.service;

import com.dealsfinder.cashbackservice.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class UserClientService {

    private static final String USER_SERVICE_URL = "http://USER-SERVICE/users/email/";

    @Autowired
    private RestTemplate restTemplate;

    public UserDTO getUserByEmail(String email) {
        try {
            String url = USER_SERVICE_URL + email;
            ResponseEntity<UserDTO> response = restTemplate.getForEntity(url, UserDTO.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch user info for email: " + email, e);
            return null;
        }
    }
}
