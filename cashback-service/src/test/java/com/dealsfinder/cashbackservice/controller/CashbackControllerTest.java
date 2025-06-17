package com.dealsfinder.cashbackservice.controller;

import com.dealsfinder.cashbackservice.entity.Cashback;
import com.dealsfinder.cashbackservice.entity.CashbackSummary;
import com.dealsfinder.cashbackservice.repository.CashbackRepository;
import com.dealsfinder.cashbackservice.repository.CashbackSummaryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class CashbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CashbackRepository cashbackRepository;

    @MockBean
    private CashbackSummaryRepository cashbackSummaryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAddCashback() throws Exception {
        Cashback cashback = Cashback.builder()
                .userEmail("user@example.com")
                .dealId(1L)
                .cashbackAmount(50.0)
                .build();

        Cashback savedCashback = Cashback.builder()
                .id(1L)
                .userEmail("user@example.com")
                .dealId(1L)
                .cashbackAmount(50.0)
                .timestamp(LocalDateTime.now())
                .build();

        CashbackSummary summary = new CashbackSummary("user@example.com", 100.0);

        Mockito.when(cashbackRepository.save(any(Cashback.class))).thenReturn(savedCashback);
        Mockito.when(cashbackSummaryRepository.findById("user@example.com")).thenReturn(Optional.of(summary));
        Mockito.when(cashbackSummaryRepository.save(any(CashbackSummary.class))).thenReturn(summary);

        mockMvc.perform(post("/cashback/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cashback)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value("user@example.com"))
                .andExpect(jsonPath("$.dealId").value(1))
                .andExpect(jsonPath("$.cashbackAmount").value(50.0));
    }

    @Test
    public void testGetCashbackForUser() throws Exception {
        String email = "user@example.com";

        Cashback cashback1 = Cashback.builder()
                .id(1L)
                .userEmail(email)
                .dealId(101L)
                .cashbackAmount(25.0)
                .timestamp(LocalDateTime.now())
                .build();

        Cashback cashback2 = Cashback.builder()
                .id(2L)
                .userEmail(email)
                .dealId(102L)
                .cashbackAmount(35.0)
                .timestamp(LocalDateTime.now())
                .build();

        Mockito.when(cashbackRepository.findByUserEmail(email)).thenReturn(List.of(cashback1, cashback2));

        mockMvc.perform(get("/cashback/user/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dealId").value(101L))
                .andExpect(jsonPath("$[0].cashbackAmount").value(25.0))
                .andExpect(jsonPath("$[1].dealId").value(102L))
                .andExpect(jsonPath("$[1].cashbackAmount").value(35.0));
    }

    @Test
    public void testGetCashbackSummary() throws Exception {
        String email = "user@example.com";
        CashbackSummary summary = new CashbackSummary(email, 120.0);

        Mockito.when(cashbackSummaryRepository.findById(email)).thenReturn(Optional.of(summary));

        mockMvc.perform(get("/cashback/summary/{email}", email))
                .andExpect(status().isOk())
                .andExpect(content().string("120.0"));
    }

    @Test
    public void testGetTotalCashbackFromTransactions() throws Exception {
        String email = "user@example.com";

        List<Cashback> cashbacks = List.of(
                new Cashback(1L, email, 201L, 10.0, LocalDateTime.now()),
                new Cashback(2L, email, 202L, 15.0, LocalDateTime.now())
        );

        Mockito.when(cashbackRepository.findByUserEmail(email)).thenReturn(cashbacks);

        mockMvc.perform(get("/cashback/user/{email}/total", email))
                .andExpect(status().isOk())
                .andExpect(content().string("25.0"));
    }
}
