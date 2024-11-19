package com.example.ShippingService.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.topic.sent-orders.partitions}")
    private int sentOrderPartitions;

    @Bean
    public NewTopic sentOrdersTopic() {
        return TopicBuilder.name("sent_orders")
                .partitions(sentOrderPartitions)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic failedShipmentsTopic() {
        return TopicBuilder.name("failed_shipments")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
