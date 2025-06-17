package com.dealsfinder.dealservice.controller;

import com.dealsfinder.dealservice.model.Deal;
import com.dealsfinder.dealservice.service.DealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DealService dealService;

    private Deal sampleDeal;

    @BeforeEach
    void setUp() {
        sampleDeal = new Deal();
        sampleDeal.setId(1L);
        sampleDeal.setTitle("Sample Deal");
        sampleDeal.setDescription("Discount on Electronics");
        sampleDeal.setDiscount(20.0);
        sampleDeal.setCategory("Electronics");
        sampleDeal.setExpiryDate(LocalDateTime.now().plusDays(5));
        sampleDeal.setActive(true);
        sampleDeal.setPrice(500.0);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllDealsForAdmin() throws Exception {
        when(dealService.getAllDeals()).thenReturn(List.of(sampleDeal));

        mockMvc.perform(get("/deals/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sample Deal"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllDeals() throws Exception {
        when(dealService.getAllDeals()).thenReturn(List.of(sampleDeal));

        mockMvc.perform(get("/deals/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sample Deal"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetDealsByCategory() throws Exception {
        when(dealService.getDealsByCategory("Electronics")).thenReturn(List.of(sampleDeal));

        mockMvc.perform(get("/deals/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Electronics"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetDealById() throws Exception {
        when(dealService.getDealById(1L)).thenReturn(Optional.of(sampleDeal));

        mockMvc.perform(get("/deals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Sample Deal"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void testGetDealById_NotFound() throws Exception {
        when(dealService.getDealById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/deals/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Deal not found with id: 999"));
    }


    @Test
    @WithMockUser(roles = "USER")
    void testGetActiveDeals() throws Exception {
        when(dealService.getActiveDeals()).thenReturn(List.of(sampleDeal));

        mockMvc.perform(get("/deals/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateDeal() throws Exception {
        when(dealService.saveDeal(any())).thenReturn(sampleDeal);

        String json = """
            {
              "title": "Sample Deal",
              "description": "Discount on Electronics",
              "discount": 20.0,
              "category": "Electronics",
              "expiryDate": "2030-12-31T23:59:59",
              "active": true,
              "price": 500.0
            }
        """;

        mockMvc.perform(post("/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Sample Deal"));
    }
}
