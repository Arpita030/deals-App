package com.dealsfinder.userservice.controller;

import com.dealsfinder.userservice.model.Deal;
import com.dealsfinder.userservice.model.User;
import com.dealsfinder.userservice.repository.UserRepository;
import com.dealsfinder.userservice.security.JwtFilter;
import com.dealsfinder.userservice.service.UserService;
import com.dealsfinder.userservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @MockBean
    private JwtFilter jwtFilter;

    @BeforeEach
    void setupJwtFilter() throws ServletException, IOException {
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            FilterChain chain = (FilterChain) args[2];
            chain.doFilter((ServletRequest) args[0], (ServletResponse) args[1]);
            return null;
        }).when(jwtFilter).doFilter(any(), any(), any());
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId("1");
        sampleUser.setEmail("test@example.com");
        sampleUser.setName("Test User");
        sampleUser.setPassword("pass");
        sampleUser.setRole("USER");
    }


    @Test
    @WithMockUser(roles = {"USER"})
    void testGetAllDeals() throws Exception {
        Deal deal = new Deal();
        deal.setTitle("Test Deal");

        when(userService.getAllDeals(anyString())).thenReturn(List.of(deal));

        mockMvc.perform(get("/users/deals")
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Test Deal"));

        verify(userService, times(1)).getAllDeals("dummy-token");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetUserById_whenExists() throws Exception {
        when(userRepository.findById("1")).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(get("/users/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetUserById_whenNotFound() throws Exception {
        when(userRepository.findById("1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/id/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with ID 1 not found"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteUser_whenExists() throws Exception {
        when(userRepository.existsById("1")).thenReturn(true);
        doNothing().when(userRepository).deleteById("1");

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteUser_whenNotExists() throws Exception {
        when(userRepository.existsById("1")).thenReturn(false);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with ID 1 not found"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateUser_whenNewUser() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@example.com\", \"name\": \"Test User\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateUser_whenEmailAlreadyExists() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(sampleUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@example.com\", \"name\": \"Test User\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User with this email already exists"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetUserByEmail_whenExists() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(sampleUser);

        mockMvc.perform(get("/users/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetUserByEmail_whenNotFound() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        mockMvc.perform(get("/users/email/test@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with email test@example.com not found"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateUser_whenExists() throws Exception {
        when(userRepository.findById("1")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User updated = invocation.getArgument(0);
            updated.setName("Updated Name");
            return updated;
        });

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@example.com\", \"name\": \"Updated Name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateUser_whenNotExists() throws Exception {
        when(userRepository.findById("1")).thenReturn(Optional.empty());

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@example.com\", \"name\": \"Updated Name\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with ID 1 not found"));
    }
}
