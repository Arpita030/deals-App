package com.dealsfinder.paymentservice.service;

import com.dealsfinder.paymentservice.config.RabbitMQConfig;
import com.dealsfinder.paymentservice.dto.CashbackMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {

    private final RabbitTemplate rabbitTemplate;

    public MessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(String message) {
        System.out.println("📤 Sending notification to RabbitMQ: " + message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_QUEUE, message);
    }

    public void sendCashback(CashbackMessage cashbackMessage) {
        System.out.println("💰 Sending cashback to RabbitMQ: " + cashbackMessage);
        rabbitTemplate.convertAndSend(RabbitMQConfig.CASHBACK_QUEUE, cashbackMessage);
    }
}
