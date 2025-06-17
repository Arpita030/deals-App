package com.dealsfinder.userservice.controller;

import com.dealsfinder.userservice.dto.UserDTO;
import com.dealsfinder.userservice.model.User;
import com.dealsfinder.userservice.service.UserService;
import com.dealsfinder.userservice.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        MockitoAnnotations.openMocks(this);
        userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john@example.com");
        userDTO.setPassword("pass123");
        userDTO.setRole("USER");

        user = new User();
        user.setId("1");
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("hashedPassword");
        user.setRole("USER");
    }

    @Test
    void testRegister_ValidUser() {
        when(userService.registerUser(any(User.class))).thenReturn(user);

        User result = authController.register(userDTO);

        assertEquals("John Doe", result.getName());
        verify(userService, times(1)).registerUser(any(User.class));
    }

    @Test
    void testRegister_InvalidRole() {
        userDTO.setRole("INVALID");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authController.register(userDTO));

        assertEquals("Invalid role. Allowed roles are USER or ADMIN.", exception.getMessage());
        verify(userService, never()).registerUser(any());
    }

    @Test
    void testLogin_ValidCredentials() {
        when(userService.findByEmail(userDTO.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(userDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getEmail(), user.getRole())).thenReturn("mockedToken");

        Map<String, String> result = authController.login(userDTO);

        assertEquals("mockedToken", result.get("token"));
        verify(jwtUtil).generateToken(user.getEmail(), user.getRole());
    }

    @Test
    void testLogin_InvalidCredentials() {
        when(userService.findByEmail(userDTO.getEmail())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> authController.login(userDTO));
    }
}
