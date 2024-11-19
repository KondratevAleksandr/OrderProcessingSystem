package com.example.OrdersService.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.topic.new-orders.partitions}")
    private int newOrdersPartitions;

    @Bean
    public NewTopic newOrdersTopic() {
        return TopicBuilder.name("new_orders")
                .partitions(newOrdersPartitions)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic failedOrdersTopic() {
        return TopicBuilder.name("failed_orders")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
