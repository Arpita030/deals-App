package com.dealsfinder.paymentservice.service;

import com.dealsfinder.dealservice.model.Deal;
import com.dealsfinder.paymentservice.client.DealClient;
import com.dealsfinder.paymentservice.dto.CashbackMessage;
import com.dealsfinder.paymentservice.entity.PaymentTransaction;
import com.dealsfinder.paymentservice.repository.PaymentTransactionRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentTransactionRepository paymentRepository;
    private final MessageSender messageSender;
    private final DealClient dealClient;

    public PaymentTransaction processPayment(PaymentTransaction paymentRequest) {
        Deal deal;
        try {
            log.info("Fetching deal for dealId: {}", paymentRequest.getDealId());
            deal = dealClient.getDealById(paymentRequest.getDealId());
            log.info("Deal fetched: {}", deal);
            if (deal == null) {
                throw new RuntimeException("Deal not found for dealId: " + paymentRequest.getDealId());
            }
            if (!deal.isActive()) {
                throw new RuntimeException("Deal is inactive for dealId: " + paymentRequest.getDealId());
            }
        } catch (FeignException.Forbidden e) {
            log.error("❌ Forbidden (403) when accessing deal service for dealId {}: {}", paymentRequest.getDealId(), e.getMessage(), e);
            throw new RuntimeException("Access to deal service forbidden. Ensure valid JWT token is provided.");
        } catch (FeignException e) {
            log.error("❌ Feign client error when accessing deal service for dealId {}: {}", paymentRequest.getDealId(), e.getMessage(), e);
            throw new RuntimeException("Failed to validate deal: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Unexpected error during deal validation for dealId {}: {}", paymentRequest.getDealId(), e.getMessage(), e);
            throw new RuntimeException("Deal validation failed: " + e.getMessage());
        }

        // Save transaction after validation
        PaymentTransaction savedPayment = paymentRepository.save(paymentRequest);

        String notification = "💳 Payment received from: " + savedPayment.getUserEmail();
        messageSender.sendNotification(notification);
        log.info("Notification sent: {}", notification);

        CashbackMessage cashbackMessage = new CashbackMessage();
        cashbackMessage.setUserEmail(savedPayment.getUserEmail());
        cashbackMessage.setDealId(savedPayment.getDealId());

        try {
            double cashback = Double.parseDouble(savedPayment.getAmount()) * 0.05;
            cashbackMessage.setCashbackAmount(cashback);
            log.info("Calculated cashback: {}", cashback);
        } catch (NumberFormatException e) {
            log.error("⚠️ Invalid amount format for cashback: {}", savedPayment.getAmount(), e);
            cashbackMessage.setCashbackAmount(0.0);
        }

        messageSender.sendCashback(cashbackMessage);
        log.info("Cashback message sent: {}", cashbackMessage);

        return savedPayment;
    }
}