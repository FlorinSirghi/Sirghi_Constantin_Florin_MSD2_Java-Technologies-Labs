package com.example.Lab4.consumer;

import com.example.Lab4.model.GradeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DLQHandler {

    private static final Logger logger = LoggerFactory.getLogger(DLQHandler.class);
    private final AtomicInteger dlqMessageCount = new AtomicInteger(0);

    @KafkaListener(topics = "${kafka.topic.dlq:grades-topic.DLT}", groupId = "${spring.kafka.consumer.group-id:prefschedule-group}-dlq")
    public void handleDLQMessage(
            @Payload(required = false) Object message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        int count = dlqMessageCount.incrementAndGet();
        
        logger.error("=== DLQ Message Received (Count: {}) ===", count);
        logger.error("Topic: {}, Partition: {}, Offset: {}", topic, partition, offset);
        logger.error("Failed Message: {}", message);
        
        System.err.println("=== DEAD LETTER QUEUE MESSAGE ===");
        System.err.println("Count: " + count);
        System.err.println("Topic: " + topic);
        System.err.println("Partition: " + partition);
        System.err.println("Offset: " + offset);
        System.err.println("Message: " + message);
        System.err.println("=================================");

        // Acknowledge DLQ message
        acknowledgment.acknowledge();
    }

    public int getDlqMessageCount() {
        return dlqMessageCount.get();
    }
}









