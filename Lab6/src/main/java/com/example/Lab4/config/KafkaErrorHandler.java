package com.example.Lab4.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class KafkaErrorHandler extends DefaultErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(KafkaErrorHandler.class);
    private static final int MAX_RETRIES = 3;
    private final Map<String, Integer> retryCountMap = new ConcurrentHashMap<>();

    @Autowired
    public KafkaErrorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
            (record, ex) -> new org.apache.kafka.common.TopicPartition(
                record.topic() + ".DLT", record.partition()));
        
        // Configure exponential backoff: initial delay 1s, max delay 3s, multiplier 2
        ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2.0);
        backOff.setMaxElapsedTime(3000L);
        
        //super(recoverer, backOff);
    }

    @Override
    public boolean handleOne(Exception exception, ConsumerRecord<?, ?> record, Consumer<?, ?> consumer, MessageListenerContainer container) {
        String recordKey = record.topic() + "-" + record.partition() + "-" + record.offset();
        int currentRetry = retryCountMap.compute(recordKey, (k, v) -> (v == null) ? 1 : v + 1);
        
        logger.warn("Error processing message (Retry {}/{}): Topic={}, Partition={}, Offset={}, Error={}", 
            currentRetry, MAX_RETRIES, record.topic(), record.partition(), record.offset(), exception.getMessage());

        if (currentRetry <= MAX_RETRIES) {
            logger.info("Retrying message processing after delay...");
            boolean handled = super.handleOne(exception, record, consumer, container);
            if (!handled) {
                // Message will be retried
                return false;
            }
        }
        
        // Max retries exceeded or non-retriable exception - send to DLQ
        logger.error("Max retries ({}) exceeded or non-retriable error. Sending to DLQ: Topic={}, Partition={}, Offset={}", 
            MAX_RETRIES, record.topic(), record.partition(), record.offset());
        
        retryCountMap.remove(recordKey);
        return super.handleOne(exception, record, consumer, container);
    }

    @Override
    public void handleOtherException(Exception exception, Consumer<?, ?> consumer, MessageListenerContainer container, boolean batchListener) {
        logger.error("Kafka consumer error", exception);
        super.handleOtherException(exception, consumer, container, batchListener);
    }
}

