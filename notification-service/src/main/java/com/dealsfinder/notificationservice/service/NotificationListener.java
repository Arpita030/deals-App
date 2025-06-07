package com.dealsfinder.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationListener {

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void handleNotification(String message) {
        log.info("📩 Received Notification: {}", message);
    }
}
