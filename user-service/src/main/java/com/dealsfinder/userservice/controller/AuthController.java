package com.dealsfinder.userservice.controller;

import com.dealsfinder.userservice.dto.UserDTO;
import com.dealsfinder.userservice.model.User;
import com.dealsfinder.userservice.service.UserService;
import com.dealsfinder.userservice.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/auth")
//public class AuthController {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @PostMapping("/register")
//    public User register(@Valid @RequestBody UserDTO userDTO) {
//        List<String> allowedRoles = List.of("USER", "ADMIN");
//        if (!allowedRoles.contains(userDTO.getRole().toUpperCase())) {
//            throw new IllegalArgumentException("Invalid role. Allowed roles are USER or ADMIN.");
//        }
//
//
//        User user = new User();
//        user.setName(userDTO.getName());
//        user.setEmail(userDTO.getEmail());
//        user.setPassword(userDTO.getPassword());
//        user.setRole(userDTO.getRole());
//        return userService.registerUser(user);
//    }
//
//    @PostMapping("/login")
//    public Map<String, String> login(@RequestBody UserDTO userDTO) {
//        User user = userService.findByEmail(userDTO.getEmail());
//        if (user == null || !passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
//            throw new RuntimeException("Invalid credentials");
//        }
//        String token = jwtUtil.generateToken(user.getEmail(),user.getRole());
//        Map<String, String> response = new HashMap<>();
//        response.put("token", token);
//        return response;
//    }
//}

import com.dealsfinder.userservice.dto.UserDTO;
import com.dealsfinder.userservice.model.User;
import com.dealsfinder.userservice.service.UserService;
import com.dealsfinder.userservice.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
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
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO, BindingResult result) {
        // Handle validation errors
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        // Validate role
        List<String> allowedRoles = List.of("USER", "ADMIN");
        if (!allowedRoles.contains(userDTO.getRole().toUpperCase())) {
            return ResponseEntity.badRequest().body(Map.of("role", "Invalid role. Allowed roles are USER or ADMIN."));
        }

        // Map DTO to Entity
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());

        return ResponseEntity.ok(userService.registerUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserDTO userDTO, BindingResult result) {
        // Handle validation errors (optional for login)
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        // Validate credentials
        User user = userService.findByEmail(userDTO.getEmail());
        if (user == null || !passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}
