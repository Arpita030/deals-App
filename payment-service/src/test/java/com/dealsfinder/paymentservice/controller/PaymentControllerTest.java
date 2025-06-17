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

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentControllerTest {

    @InjectMocks
    private PaymentController paymentController;

    @Mock
    private BraintreeGateway braintreeGateway;

    @Mock
    private TransactionGateway transactionGateway;

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private PaymentService paymentService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        paymentController = new PaymentController(braintreeGateway, paymentTransactionRepository);
        Field field = PaymentController.class.getDeclaredField("paymentService");
        field.setAccessible(true);
        field.set(paymentController, paymentService);

        when(braintreeGateway.transaction()).thenReturn(transactionGateway);
    }



    @Test
    void testCheckout_SuccessfulPayment() {
        PaymentRequestDto requestDto = new PaymentRequestDto("fake-nonce", "100.00", "user@example.com", 1L);

        Transaction transaction = mock(Transaction.class);
        when(transaction.getId()).thenReturn("txn123");
        when(transaction.getStatus()).thenReturn(Transaction.Status.SUBMITTED_FOR_SETTLEMENT);
        when(transaction.getAmount()).thenReturn(new BigDecimal("100.00"));
        when(transaction.getPaymentInstrumentType()).thenReturn("fake-card");

        Result<Transaction> result = mock(Result.class);
        when(result.isSuccess()).thenReturn(true);
        when(result.getTarget()).thenReturn(transaction);

        when(transactionGateway.sale(any(TransactionRequest.class))).thenReturn(result);

        when(paymentService.processPayment(any(PaymentTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<String> response = paymentController.checkout(requestDto);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Transaction successful"));
    }

    @Test
    void testCheckout_InvalidAmount() {
        PaymentRequestDto requestDto = new PaymentRequestDto("fake-nonce", "0", "user@example.com", 1L);
        ResponseEntity<String> response = paymentController.checkout(requestDto);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Invalid amount"));
    }

    @Test
    void testCheckout_MissingNonce() {
        PaymentRequestDto requestDto = new PaymentRequestDto("", "100.00", "user@example.com", 1L);
        ResponseEntity<String> response = paymentController.checkout(requestDto);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Missing payment method nonce"));
    }

    @Test
    void testGetUserTransactions() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@example.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<PaymentTransaction> mockTransactions = List.of(
                PaymentTransaction.builder()
                        .transactionId("txn1")
                        .userEmail("user@example.com")
                        .dealId(1L)
                        .status("SUCCESS")
                        .amount("100.00")
                        .paymentMethod("card")
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        when(paymentTransactionRepository.findByUserEmail("user@example.com")).thenReturn(mockTransactions);

        ResponseEntity<List<PaymentTransaction>> response = paymentController.getUserTransactions();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("txn1", response.getBody().get(0).getTransactionId());
    }

    @Test
    void testGetAllTransactions() {
        List<PaymentTransaction> allTransactions = List.of(
                PaymentTransaction.builder()
                        .transactionId("txn2")
                        .userEmail("admin@example.com")
                        .dealId(2L)
                        .status("SUCCESS")
                        .amount("200.00")
                        .paymentMethod("paypal")
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        when(paymentTransactionRepository.findAll()).thenReturn(allTransactions);

        ResponseEntity<List<PaymentTransaction>> response = paymentController.getAllTransactions();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("txn2", response.getBody().get(0).getTransactionId());
    }
}
