package com.example.NotificationsService.Kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {
    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public NotificationConsumer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "sent_orders", groupId = "notification_service_group", concurrency = "3")
    public void consume(String shippingInfo) {
        logger.info("Dear user! Order has been successfully delivered! Order details: {}", shippingInfo);

        // Отправка уведомлений
        try {
            // Здесь по хорошему добавить бы логику отправки уведомлений (например, через email или SMS)
            logger.info("Notification sent for order: {}", shippingInfo);
        } catch (Exception e) {
            logger.error("Error sending notification for order {}: {}", shippingInfo, e.getMessage());
            sendFailedNotification(shippingInfo);
        }
    }

    private void sendFailedNotification(String shippingInfo) {
        logger.info("Sending failed notification info: {}", shippingInfo);
        kafkaTemplate.send("failed_notifications", shippingInfo);
        logger.info("Failed notification info sent to topic 'failed_notifications'");
    }
}