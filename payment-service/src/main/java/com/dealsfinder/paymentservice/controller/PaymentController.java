package com.dealsfinder.paymentservice.controller;

import com.braintreegateway.*;
import com.dealsfinder.paymentservice.dto.PaymentRequestDto;
import com.dealsfinder.paymentservice.entity.PaymentTransaction;
import com.dealsfinder.paymentservice.repository.PaymentTransactionRepository;
import com.dealsfinder.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final BraintreeGateway gateway;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestBody PaymentRequestDto request) {
        if (request.getAmount() == null || new BigDecimal(request.getAmount()).compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("❌ Invalid amount.");
        }

        if (request.getPaymentMethodNonce() == null || request.getPaymentMethodNonce().isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Missing payment method nonce.");
        }

        TransactionRequest transactionRequest = new TransactionRequest()
                .amount(new BigDecimal(request.getAmount()))
                .paymentMethodNonce(request.getPaymentMethodNonce())
                .options()
                .submitForSettlement(true)
                .done();

        Result<Transaction> result = gateway.transaction().sale(transactionRequest);

        if (result.isSuccess()) {
            Transaction transaction = result.getTarget();

            PaymentTransaction paymentRecord = PaymentTransaction.builder()
                    .transactionId(transaction.getId())
                    .status(transaction.getStatus().toString())
                    .amount(transaction.getAmount().toPlainString())
                    .paymentMethod(transaction.getPaymentInstrumentType())
                    .createdAt(LocalDateTime.now())
                    .userEmail(request.getUserEmail())
                    .dealId(request.getDealId())
                    .build();

            paymentTransactionRepository.save(paymentRecord);

            paymentService.processPayment(paymentRecord);

            return ResponseEntity.ok("✅ Transaction successful. ID: " + transaction.getId());
        } else {
            return ResponseEntity.status(500).body("❌ Error: " + result.getMessage());
        }
    }

    @GetMapping("/user/transactions")
    public ResponseEntity<List<PaymentTransaction>> getUserTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        List<PaymentTransaction> transactions = paymentTransactionRepository.findByUserEmail(userEmail);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<PaymentTransaction>> getAllTransactions() {
        List<PaymentTransaction> allTransactions = paymentTransactionRepository.findAll();
        return ResponseEntity.ok(allTransactions);
    }
}
