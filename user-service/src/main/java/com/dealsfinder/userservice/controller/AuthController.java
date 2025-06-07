package com.dealsfinder.userservice.controller;

import com.dealsfinder.userservice.dto.UserDTO;
import com.dealsfinder.userservice.model.User;
import com.dealsfinder.userservice.service.UserService;
import com.dealsfinder.userservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public User register(@RequestBody UserDTO userDTO) {
        List<String> allowedRoles = List.of("USER", "ADMIN");
        if (!allowedRoles.contains(userDTO.getRole().toUpperCase())) {
            throw new IllegalArgumentException("Invalid role. Allowed roles are USER or ADMIN.");
        }


        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody UserDTO userDTO) {
        User user = userService.findByEmail(userDTO.getEmail());
        if (user == null || !passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getEmail(),user.getRole());
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }
}
