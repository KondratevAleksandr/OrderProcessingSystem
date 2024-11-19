package com.example.PaymentService.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumer {
    private static final Logger logger = LoggerFactory.getLogger(PaymentConsumer.class);
    private final PaymentProducer paymentProducer;

    @Autowired
    public PaymentConsumer(PaymentProducer paymentProducer) {
        this.paymentProducer = paymentProducer;
    }

    @KafkaListener(topics = "new_orders", groupId = "payment_service_group", concurrency = "3")
    public void consume(String order) {
        logger.info("Received order for payment: {}", order);

        //обрабатываем платеж
        String paymentInfo;
        try {
            paymentInfo = "Payment successful for: " + order;
            paymentProducer.sendPayment(paymentInfo);
            logger.info("Payment processed and sent to 'payed_orders' topic");
        } catch (Exception e) {
            logger.error("Error processing payment for order {}: {}", order, e.getMessage());
            paymentProducer.sendFailedPayment(order);
        }
    }
}
