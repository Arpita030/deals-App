package com.dealsfinder.dealservice.controller;

import com.dealsfinder.dealservice.model.Deal;
import com.dealsfinder.dealservice.service.DealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DealControllerTest {

    @InjectMocks
    private DealController dealController;

    @Mock
    private DealService dealService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dealController).build();
    }

    private Deal createSampleDeal(Long id) {
        Deal deal = new Deal();
        deal.setId(id);
        deal.setTitle("Deal " + id);
        deal.setDescription("Description " + id);
        deal.setDiscount(10.0);
        deal.setCategory("Electronics");
        deal.setExpiryDate(LocalDateTime.now().plusDays(10));
        deal.setActive(true);
        deal.setPrice(1000.0);
        return deal;
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetAllDealsForAdmin() throws Exception {
        List<Deal> deals = Arrays.asList(createSampleDeal(1L), createSampleDeal(2L));
        when(dealService.getAllDeals()).thenReturn(deals);

        mockMvc.perform(get("/deals/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(dealService, times(1)).getAllDeals();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetAllDeals() throws Exception {
        List<Deal> deals = Arrays.asList(createSampleDeal(1L));
        when(dealService.getAllDeals()).thenReturn(deals);

        mockMvc.perform(get("/deals/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(dealService, times(1)).getAllDeals();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetDealsByCategory() throws Exception {
        String category = "Electronics";
        List<Deal> deals = Arrays.asList(createSampleDeal(1L));
        when(dealService.getDealsByCategory(category)).thenReturn(deals);

        mockMvc.perform(get("/deals/category/{category}", category))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value(category));

        verify(dealService, times(1)).getDealsByCategory(category);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetDealById() throws Exception {
        Deal deal = createSampleDeal(1L);
        when(dealService.getDealById(1L)).thenReturn(Optional.of(deal));

        mockMvc.perform(get("/deals/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(dealService, times(1)).getDealById(1L);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetDealById_NotFound() throws Exception {
        when(dealService.getDealById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/deals/{id}", 1L))
                .andExpect(status().isInternalServerError())
                .andExpect(status().reason("Deal not found with id: 1"));

        verify(dealService, times(1)).getDealById(1L);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetActiveDeals() throws Exception {
        List<Deal> deals = Arrays.asList(createSampleDeal(1L));
        when(dealService.getActiveDeals()).thenReturn(deals);

        mockMvc.perform(get("/deals/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isActive").value(true));

        verify(dealService, times(1)).getActiveDeals();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateDeal() throws Exception {
        Deal deal = createSampleDeal(1L);
        when(dealService.saveDeal(any(Deal.class))).thenReturn(deal);

        String dealJson = """
                {
                  "title":"Deal 1",
                  "description":"Description 1",
                  "discount":10.0,
                  "category":"Electronics",
                  "expiryDate":"%s",
                  "active":true,
                  "price":1000.0
                }
                """.formatted(deal.getExpiryDate().toString());

        mockMvc.perform(post("/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dealJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Deal 1"));

        verify(dealService, times(1)).saveDeal(any(Deal.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateDeal() throws Exception {
        Deal updatedDeal = createSampleDeal(1L);
        updatedDeal.setTitle("Updated Deal");
        when(dealService.updateDeal(eq(1L), any(Deal.class))).thenReturn(updatedDeal);

        String updatedDealJson = """
                {
                  "title":"Updated Deal",
                  "description":"Description 1",
                  "discount":10.0,
                  "category":"Electronics",
                  "expiryDate":"%s",
                  "active":true,
                  "price":1000.0
                }
                """.formatted(updatedDeal.getExpiryDate().toString());

        mockMvc.perform(put("/deals/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedDealJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Deal"));

        verify(dealService, times(1)).updateDeal(eq(1L), any(Deal.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteDeal() throws Exception {
        doNothing().when(dealService).deleteDeal(1L);

        mockMvc.perform(delete("/deals/{id}", 1L))
                .andExpect(status().isOk());

        verify(dealService, times(1)).deleteDeal(1L);
    }
}
