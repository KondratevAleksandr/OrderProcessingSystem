package com.example.ShippingService.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ShippingConsumer {
    private static final Logger logger = LoggerFactory.getLogger(ShippingConsumer.class);
    private final ShippingProducer shippingProducer;

    @Autowired
    public ShippingConsumer(ShippingProducer shippingProducer) {
        this.shippingProducer = shippingProducer;
    }

    @KafkaListener(topics = "payed_orders", groupId = "shipping_service_group", concurrency = "3")
    public void consume(String paymentInfo) {
        logger.info("Preparing shipment for: {}", paymentInfo);

        // Обработка отгрузки
        String shippingInfo;
        try {
            shippingInfo = "Shipment sent for: " + paymentInfo;
            shippingProducer.sendShippingInfo(shippingInfo);
            logger.info("Shipment processed and sent to 'sent_orders' topic");
        } catch (Exception e) {
            logger.error("Error processing shipment for payment info {}: {}", paymentInfo, e.getMessage());
            shippingProducer.sendFailedShipment(paymentInfo);
        }
    }
}