package com.dealsfinder.userservice.controller;

import com.dealsfinder.userservice.exception.UserNotFoundException;
import com.dealsfinder.userservice.model.Deal;
import com.dealsfinder.userservice.model.User;
import com.dealsfinder.userservice.repository.UserRepository;
import com.dealsfinder.userservice.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId("1");
        user.setName("Arpita");
        user.setEmail("arpita@example.com");
    }

    @Test
    void testGetAllDeals() {
        String token = "token123";
        Deal deal = new Deal();
        deal.setId(101L);
        when(userService.getAllDeals(token)).thenReturn(List.of(deal));

        List<Deal> deals = userController.getAllDeals("Bearer " + token);
        assertEquals(1, deals.size());
        assertEquals(101L, deals.get(0).getId());
    }

    @Test
    void testGetProfile_userExists() {
        when(authentication.getName()).thenReturn("arpita@example.com");
        when(userRepository.findByEmail("arpita@example.com")).thenReturn(user);

        ResponseEntity<User> response = userController.getProfile(authentication);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Arpita", response.getBody().getName());
    }

    @Test
    void testGetProfile_userNotFound() {
        when(authentication.getName()).thenReturn("unknown@example.com");
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userController.getProfile(authentication));
    }

    @Test
    void testGetUserById_userFound() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUser("1");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Arpita", response.getBody().getName());
    }

    @Test
    void testGetUserById_userNotFound() {
        when(userRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userController.getUser("99"));
    }

    @Test
    void testCreateUser_userDoesNotExist() {
        when(userRepository.findByEmail("arpita@example.com")).thenReturn(null);
        when(userRepository.save(user)).thenReturn(user);

        ResponseEntity<?> response = userController.createUser(user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

    @Test
    void testCreateUser_userAlreadyExists() {
        when(userRepository.findByEmail("arpita@example.com")).thenReturn(user);

        ResponseEntity<?> response = userController.createUser(user);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).get("error").toString().contains("already exists"));
    }

    @Test
    void testGetUserByEmail_userFound() {
        when(userRepository.findByEmail("arpita@example.com")).thenReturn(user);

        ResponseEntity<User> response = userController.getUserByEmail("arpita@example.com");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Arpita", response.getBody().getName());
    }

    @Test
    void testGetUserByEmail_userNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userController.getUserByEmail("notfound@example.com"));
    }

    @Test
    void testUpdateUser_userFound() {
        User updated = new User();
        updated.setName("Updated");
        updated.setEmail("updated@example.com");

        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updated);

        ResponseEntity<User> response = userController.updateUser("1", updated);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated", response.getBody().getName());
    }

    @Test
    void testUpdateUser_userNotFound() {
        when(userRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userController.updateUser("99", user));
    }

    @Test
    void testDeleteUser_userExists() {
        when(userRepository.existsById("1")).thenReturn(true);
        doNothing().when(userRepository).deleteById("1");

        ResponseEntity<String> response = userController.deleteUser("1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User deleted successfully", response.getBody());
        verify(userRepository).deleteById("1");
    }

    @Test
    void testDeleteUser_userNotFound() {
        when(userRepository.existsById("99")).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userController.deleteUser("99"));
    }
}
