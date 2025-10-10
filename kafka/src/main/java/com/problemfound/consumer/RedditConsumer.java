package com.problemfound.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.problemfound.dto.ProblemDTO;
import com.problemfound.dto.ProblemKeywordDTO;
import com.problemfound.model.Problem;
import com.problemfound.service.ProblemService;
import com.problemfound.service.RedditService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class RedditConsumer {

    private final ObjectMapper objectMapper;
    private final ProblemService problemService;
    private final RedditService redditService;

    public RedditConsumer(ObjectMapper objectMapper, ProblemService problemService, RedditService redditService) {
        this.objectMapper = objectMapper;
        this.problemService = problemService;
        this.redditService = redditService;
    }

    @KafkaListener(topics = "reddit.problems_keywords",groupId = "group")
    public void consume(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            ProblemDTO problemDTO = new ProblemDTO(
                    node.get("id").asText(),
                    "reddit",
                    node.get("text").asText(),
                    node.get("url").asText(),
                    OffsetDateTime.parse(node.get("timeStamp").asText()).toLocalDateTime(),
                    parseKeywords(node)
            );

            Problem problem = problemService.createProblem(problemDTO);
            redditService.createRedditInfo(
                    problem,
                    node.get("id").asText(),
                    node.get("subreddit").asText(),
                    node.get("post").asText(),
                    node.get("comment").asText()
                    );
        } catch (Exception e) {
            System.err.println("Failed to process message: " + e.getMessage());
        }
        System.out.println("Received: " + message);
    }

    private List<ProblemKeywordDTO> parseKeywords(JsonNode node) {
        List<ProblemKeywordDTO> keywords = new ArrayList<ProblemKeywordDTO>();
        if (node.has("keywords")) {
            for (JsonNode kwArray: node.get("keywords")) {
                keywords.add(new ProblemKeywordDTO(kwArray.get(0).asText(),kwArray.get(1).asDouble()));
            }
        }
        return keywords;
    }
}
