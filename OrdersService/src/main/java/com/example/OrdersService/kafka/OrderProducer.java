package com.example.OrdersService.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {
    private static final Logger logger = LoggerFactory.getLogger(OrderProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RetryTemplate retryTemplate;

    @Autowired
    public OrderProducer(KafkaTemplate<String, String> kafkaTemplate, RetryTemplate retryTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.retryTemplate = retryTemplate;
    }

    @Async
    public void sendOrder(String order) {
        logger.info("Sending order {}", order);

        retryTemplate.execute(context -> {
            kafkaTemplate.send("new_orders", order);
            logger.info("Order sent successfully to topic 'new_orders'");
            return null;
        }, context -> {
            logger.error("Failed to send order to topic 'new_orders' after {} attempts", context.getRetryCount());
            //можно добавить логику для обработки неудачных отправок
            // но пока не знаю как :) По этому я буду их сохранять в тему "failed_orders"
            kafkaTemplate.send("failed_orders", order);
            logger.info("Order sent to 'failed_orders' topic for further processing");
            return null;
        });
    }
}
