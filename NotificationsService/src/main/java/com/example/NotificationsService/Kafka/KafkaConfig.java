package com.example.NotificationsService.Kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.topic.failed-notifications.partitions}")
    private int failedNotificationsPartitions;

    @Bean
    public NewTopic failedNotificationsTopic() {
        return TopicBuilder.name("failed_notifications")
                .partitions(failedNotificationsPartitions)
                .replicas(1)
                .build();
    }
}
