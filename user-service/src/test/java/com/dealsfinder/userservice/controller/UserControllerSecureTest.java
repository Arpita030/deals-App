package com.dealsfinder.userservice.controller;

import com.dealsfinder.userservice.model.User;
import com.dealsfinder.userservice.repository.UserRepository;
import com.dealsfinder.userservice.security.JwtFilter;
import com.dealsfinder.userservice.service.UserService;
import com.dealsfinder.userservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({JwtUtil.class})
@AutoConfigureMockMvc
class UserControllerSecureTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtFilter jwtFilter;

    @BeforeEach
    void setupJwtFilter() throws Exception {
        doAnswer(invocation -> {
            FilterChain chain = (FilterChain) invocation.getArguments()[2];
            chain.doFilter((ServletRequest) invocation.getArguments()[0], (ServletResponse) invocation.getArguments()[1]);
            return null;
        }).when(jwtFilter).doFilter(any(), any(), any());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void testGetProfile_whenUserExists() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void testGetProfile_whenUserNotFound() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with email test@example.com not found"));
    }
}
