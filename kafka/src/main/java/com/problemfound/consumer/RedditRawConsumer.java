package com.problemfound.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.problemfound.producer.MessageProducer;
import com.problemfound.service.RedditService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RedditRawConsumer {

    private final ObjectMapper objectMapper;
    private final RedditService redditService;

    private final MessageProducer messageProducer;

    public RedditRawConsumer(ObjectMapper objectMapper, RedditService redditService, MessageProducer messageProducer) {
        this.objectMapper = objectMapper;
        this.redditService = redditService;
        this.messageProducer = messageProducer;
    }

    @KafkaListener(topics = "reddit.raw",groupId = "group")
    public void consume(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            String reddit_id = node.get("id").asText();
            if (!redditService.checkIfRedditExists(reddit_id)) {
                System.out.println("New message: " + reddit_id);
                messageProducer.sendMessage("reddit.raw_new",message);
            } else {
                System.out.println("Old message: " + reddit_id);
            }
        } catch (Exception e) {
            System.err.println("Failed to process message: " + e.getMessage());
        }
    }
}
