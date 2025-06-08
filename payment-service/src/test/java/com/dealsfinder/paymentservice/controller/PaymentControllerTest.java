package com.dealsfinder.paymentservice.controller;

import com.braintreegateway.*;
import com.dealsfinder.paymentservice.dto.PaymentRequestDto;
import com.dealsfinder.paymentservice.entity.PaymentTransaction;
import com.dealsfinder.paymentservice.repository.PaymentTransactionRepository;
import com.dealsfinder.paymentservice.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentControllerTest {

    @Mock
    private BraintreeGateway gateway;

    @Mock
    private TransactionGateway transactionGateway;

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(gateway.transaction()).thenReturn(transactionGateway);
    }

    @Test
    void testCheckoutSuccess() {
        PaymentRequestDto request = new PaymentRequestDto();
        request.setAmount("10.00");
        request.setPaymentMethodNonce("fake-valid-nonce");
        request.setUserEmail("user@example.com");
        request.setDealId(123L);

        Transaction transaction = mock(Transaction.class);
        when(transaction.getId()).thenReturn("txn-id");
        when(transaction.getStatus()).thenReturn(Transaction.Status.SETTLED);
        when(transaction.getAmount()).thenReturn(new BigDecimal("10.00"));
        when(transaction.getPaymentInstrumentType()).thenReturn("credit_card");

        Result<Transaction> result = mock(Result.class);
        when(result.isSuccess()).thenReturn(true);
        when(result.getTarget()).thenReturn(transaction);
        when(transactionGateway.sale(any(TransactionRequest.class))).thenReturn(result);

        ResponseEntity<String> response = paymentController.checkout(request);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Transaction successful"));
        verify(paymentService).processPayment(any(PaymentTransaction.class));
    }

    @Test
    void testCheckoutInvalidAmount() {
        PaymentRequestDto request = new PaymentRequestDto();
        request.setAmount("0");
        request.setPaymentMethodNonce("some-nonce");

        ResponseEntity<String> response = paymentController.checkout(request);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid amount.", response.getBody());
    }

    @Test
    void testCheckoutMissingNonce() {
        PaymentRequestDto request = new PaymentRequestDto();
        request.setAmount("10.00");
        request.setPaymentMethodNonce("");

        ResponseEntity<String> response = paymentController.checkout(request);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Missing payment method nonce.", response.getBody());
    }

    @Test
    void testCheckoutBraintreeFailure() {
        PaymentRequestDto request = new PaymentRequestDto();
        request.setAmount("10.00");
        request.setPaymentMethodNonce("fake-nonce");

        Result<Transaction> result = mock(Result.class);
        when(result.isSuccess()).thenReturn(false);
        when(result.getMessage()).thenReturn("Braintree error");
        when(transactionGateway.sale(any(TransactionRequest.class))).thenReturn(result);

        ResponseEntity<String> response = paymentController.checkout(request);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Error: Braintree error", response.getBody());
    }

    @Test
    void testGetUserTransactions() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@example.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        PaymentTransaction tx = new PaymentTransaction("txn-id", "SETTLED",     ``"10.00", "credit_card",
                LocalDateTime.now(), "user@example.com", 123L);
        when(paymentTransactionRepository.findByUserEmail("user@example.com")).thenReturn(List.of(tx));

        ResponseEntity<List<PaymentTransaction>> response = paymentController.getUserTransactions();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("txn-id", response.getBody().get(0).getTransactionId());
    }

    @Test
    void testGetAllTransactions() {
        PaymentTransaction tx = new PaymentTransaction("txn-id", "SETTLED", "10.00", "credit_card",
                LocalDateTime.now(), "user@example.com", 123L);
        when(paymentTransactionRepository.findAll()).thenReturn(Collections.singletonList(tx));

        ResponseEntity<List<PaymentTransaction>> response = paymentController.getAllTransactions();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }
}
