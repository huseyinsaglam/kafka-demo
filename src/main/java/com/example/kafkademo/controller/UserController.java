package com.example.kafkademo.controller;


import com.example.kafkademo.domain.model.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@AllArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String topicName = "etiya.csm-kafka-demo-user-created.0";

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User request) throws ExecutionException, InterruptedException {

        request.setId(UUID.randomUUID().toString());

        Message<User> userMsg =
                MessageBuilder.withPayload(request)
                        .setHeader(KafkaHeaders.TOPIC, topicName)
                        // .setHeader(KafkaHeaders.KEY, user.getUserName())
                        // .setHeader(KafkaHeaders.PARTITION, 1)
                        //.setHeader("X-AgentName", "kafka-demo-app")
                        .build();


        kafkaTemplate.send(userMsg).get();
        // kafkaTemplate.send(topicName, userMsg).get();

        return new ResponseEntity<>(request, HttpStatus.CREATED);

    }

    @GetMapping()
    public String getTopicName(){
        return topicName;
    }

}
