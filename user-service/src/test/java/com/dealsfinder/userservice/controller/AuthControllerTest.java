package com.dealsfinder.userservice.controller;

import com.dealsfinder.userservice.dto.UserDTO;
import com.dealsfinder.userservice.model.User;
import com.dealsfinder.userservice.service.UserService;
import com.dealsfinder.userservice.util.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private UserDTO userDTO;
    private User user;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setName("Arpita");
        userDTO.setEmail("arpita@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole("USER");

        user = new User();
        user.setId("1");
        user.setName("Arpita");
        user.setEmail("arpita@example.com");
        user.setPassword("encodedPassword123");
        user.setRole("USER");
    }

    @Test
    void testRegister_ValidRole() {
        when(userService.registerUser(any(User.class))).thenReturn(user);

        User result = authController.register(userDTO);

        assertNotNull(result);
        assertEquals("Arpita", result.getName());
        verify(userService).registerUser(any(User.class));
    }

    @Test
    void testRegister_InvalidRole() {
        userDTO.setRole("INVALID_ROLE");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authController.register(userDTO);
        });

        assertEquals("Invalid role. Allowed roles are USER or ADMIN.", exception.getMessage());
    }

    @Test
    void testLogin_ValidCredentials() {
        when(userService.findByEmail("arpita@example.com")).thenReturn(user);
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(jwtUtil.generateToken("arpita@example.com", "USER")).thenReturn("fake-jwt-token");

        Map<String, String> response = authController.login(userDTO);

        assertEquals("fake-jwt-token", response.get("token"));
    }

    @Test
    void testLogin_InvalidEmail() {
        when(userService.findByEmail("arpita@example.com")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> authController.login(userDTO));
    }

    @Test
    void testLogin_InvalidPassword() {
        when(userService.findByEmail("arpita@example.com")).thenReturn(user);
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authController.login(userDTO));
    }
}
