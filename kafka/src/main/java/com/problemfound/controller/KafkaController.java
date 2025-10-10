package com.problemfound.controller;

import com.problemfound.producer.MessageProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("kafka")
public class KafkaController {

    private final MessageProducer messageProducer;

    public KafkaController(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }
    @PostMapping("/send")
    public String sendMessage(@RequestParam("message") String message) {
        messageProducer.sendMessage("reddit.raw",message);
        return "Message sent " + message;
    }
}
