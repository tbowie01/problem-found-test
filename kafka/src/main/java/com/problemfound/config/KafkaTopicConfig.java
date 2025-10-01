package com.problemfound.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

public class KafkaTopicConfig {
    @Bean
    public NewTopic redditRawTopic(){
        return TopicBuilder.name("reddit.raw")
                .replicas(1)
                .partitions(3)
                .build();
    }
}
