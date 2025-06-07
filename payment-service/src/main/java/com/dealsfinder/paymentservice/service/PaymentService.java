package com.dealsfinder.paymentservice.service;

import com.dealsfinder.paymentservice.dto.CashbackMessage;
import com.dealsfinder.paymentservice.entity.PaymentTransaction;
import com.dealsfinder.paymentservice.repository.PaymentTransactionRepository;
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

    public PaymentTransaction processPayment(PaymentTransaction paymentRequest) {
        // Save transaction (already has transactionId and createdAt set in controller)
        PaymentTransaction savedPayment = paymentRepository.save(paymentRequest);

        // Send notification to Notification Service via RabbitMQ
        String notification = "💳 Payment received from: " + savedPayment.getUserEmail();
        messageSender.sendNotification(notification);
        log.info("Notification sent: {}", notification);

        // Prepare cashback message
        CashbackMessage cashbackMessage = new CashbackMessage();
        cashbackMessage.setUserEmail(savedPayment.getUserEmail());
        cashbackMessage.setDealId(savedPayment.getDealId());

        try {
            double cashback = Double.parseDouble(savedPayment.getAmount()) * 0.05;
            cashbackMessage.setCashbackAmount(cashback);
            log.info("Calculated cashback: {}", cashback);
        } catch (NumberFormatException e) {
            log.error("⚠️ Invalid amount format for cashback: {}", savedPayment.getAmount(), e);
            cashbackMessage.setCashbackAmount(0.0); // fallback
        }

        // Send cashback message to Cashback Service via RabbitMQ
        messageSender.sendCashback(cashbackMessage);
        log.info("Cashback message sent: {}", cashbackMessage);

        return savedPayment;
    }
}
