package com.example.PaymentService.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducer {
    private static final Logger logger = LoggerFactory.getLogger(PaymentProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RetryTemplate retryTemplate;

    @Autowired
    public PaymentProducer(KafkaTemplate<String, String> kafkaTemplate, RetryTemplate retryTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.retryTemplate = retryTemplate;
    }

    @Async
    public void sendPayment(String paymentInfo) {
        logger.info("Sending payment info: {}", paymentInfo);

        retryTemplate.execute(context -> {
            kafkaTemplate.send("payed_orders", paymentInfo);
            logger.info("Payment info sent successfully to topic 'payed_orders'");
            return null;
        }, context -> {
            logger.error("Failed to sent payment info to topic 'payed_orders'" +
                    "after {} attempts", context.getRetryCount());
            kafkaTemplate.send("failed_payments", paymentInfo);
            logger.info("Payment info sent to 'failed_payments' topic for further processing.");
            return null;
        });
    }

    public void sendFailedPayment(String order) {
        String failedPaymentInfo = "Failed payment for order: " + order;
        logger.info("Sending failed payment info: {}", failedPaymentInfo);
        kafkaTemplate.send("failed_payments", failedPaymentInfo);
        logger.info("Failed payment info sent to topic 'failed_payments'");
    }
}
