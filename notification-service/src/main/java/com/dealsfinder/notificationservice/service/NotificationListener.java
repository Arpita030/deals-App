//package com.dealsfinder.notificationservice.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//public class NotificationListener {
//
//    @RabbitListener(queues = "${app.rabbitmq.queue}")
//    public void handleNotification(String message) {
//        log.info("📩 Received Notification: {}", message);
//    }
//}
//package com.dealsfinder.notificationservice.service;
//
//import com.dealsfinder.notificationservice.model.NotificationMessage;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class NotificationListener {
//
//    private final EmailService emailService;
//
//    @RabbitListener(queues = "${app.rabbitmq.queue}")
//    public void handleNotification(NotificationMessage message) {
//        try {
//            log.info("📩 Received Notification: {}", message);
//
//            if ("EMAIL".equalsIgnoreCase(message.getType())) {
//                emailService.sendEmail(
//                        message.getRecipient(),
//                        "🎉 Cashback Confirmation - DealsFinder",
//                        message.getMessage()
//                );
//                log.info("✅ Email sent to {}", message.getRecipient());
//            } else {
//                log.info("🔕 Notification type is not EMAIL. Skipping email send.");
//            }
//        } catch (Exception e) {
//            log.error("❌ Failed to process notification message", e);
//        }
//    }
//}
package com.dealsfinder.notificationservice.service;

import com.dealsfinder.notificationservice.model.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationListener {

    private final EmailService emailService;

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void handleNotification(NotificationMessage message) {
        try {
            log.info("📩 Received Notification: {}", message);

            emailService.sendEmail(
                    message.getRecipient(),
                    message.getSubject(),
                    message.getMessage()
            );
            log.info("✅ Email sent to {}", message.getRecipient());

        } catch (Exception e) {
            log.error("❌ Failed to process notification message", e);
        }
    }
}

