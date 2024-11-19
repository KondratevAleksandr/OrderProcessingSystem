package com.example.ShippingService.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ShippingProducer {
    private static final Logger logger = LoggerFactory.getLogger(ShippingProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RetryTemplate retryTemplate;

    @Autowired
    public ShippingProducer(KafkaTemplate<String, String> kafkaTemplate, RetryTemplate retryTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.retryTemplate = retryTemplate;
    }

    @Async
    public void sendShippingInfo(String shippingInfo) {
        logger.info("Sending shipping info: {}", shippingInfo);

        retryTemplate.execute(context -> {
            kafkaTemplate.send("sent_orders", shippingInfo);
            logger.info("Shipping info sent successfully to topic 'sent_orders'");
            return null;
        }, context -> {
            logger.error("Failed to send shipping info to topic 'sent_orders' after {} attempts", context.getRetryCount());
            kafkaTemplate.send("failed_shipments", shippingInfo);
            logger.info("Shipping info sent to 'failed_shipments' topic for further processing.");
            return null;
        });
    }

    public void sendFailedShipment(String paymentInfo) {
        String failedShipmentInfo = "Failed shipment for payment info: " + paymentInfo;
        logger.info("Sending failed shipment info: {}", failedShipmentInfo);
        kafkaTemplate.send("failed_shipments", failedShipmentInfo);
        logger.info("Failed shipment info sent to topic 'failed_shipments'");
    }
}
