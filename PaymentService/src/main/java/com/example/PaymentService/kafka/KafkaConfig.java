package com.example.PaymentService.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.topic.payed-orders.partitions}")
    private int payedOrdersPartitions;

    @Bean
    public NewTopic payedOrdersTopic() {
        return TopicBuilder.name("payed_orders")
                .partitions(payedOrdersPartitions)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic failedPaymentsTopic() {
        return TopicBuilder.name("failed_payments")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
