package com.example.kafkademo.consumers;

import com.example.kafkademo.domain.exception.MyCustomException;
import com.example.kafkademo.domain.model.User;
import com.example.kafkademo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCreatedConsumer {
    private final UserRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    @KafkaListener(topics = "etiya.csm-kafka-demo-user-created.0", containerFactory = "kafkaListenerContainerFactory")
    public void consume(
            @Payload User user,
            @Header(KafkaHeaders.OFFSET) int offset,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
    ) throws Exception {
        log.info("userId: {}, partition: {}, offset: {}", user.getId(), partition, offset);

        Thread.sleep(1000);
        handle(user);
    }

    private void handle(User user) throws MyCustomException {

        if(user.getUserName().isEmpty()){
            throw new MyCustomException("userName is null");
        }

        repository.save(user);
        kafkaTemplate.send("etiya.csm-kafka-demo-user-validation.0", user);
    }


    @KafkaListener(topics = "etiya.csm-kafka-demo-user-created.0.error", containerFactory = "kafkaListenerContainerFactory")
    public void consumeError(@Payload User user) throws Exception {
        log.info("Error event received, userName: {}", user.getUserName());
        Thread.sleep(1000);
        handle(user);
    }

}
