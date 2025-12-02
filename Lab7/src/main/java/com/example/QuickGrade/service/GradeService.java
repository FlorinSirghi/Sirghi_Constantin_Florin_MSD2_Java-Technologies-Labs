package com.example.QuickGrade.service;

import com.example.QuickGrade.model.GradeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class GradeService {

    private static final Logger logger = LoggerFactory.getLogger(GradeService.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.grades:grades-topic}")
    private String gradesTopic;

    public void publishGrade(GradeEvent gradeEvent) {
        try {
            String key = gradeEvent.getStudentCode() + "-" + gradeEvent.getCourseCode();
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(gradesTopic, key, gradeEvent);
            
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    logger.info("Grade event published successfully: studentCode={}, courseCode={}, grade={}", 
                        gradeEvent.getStudentCode(), gradeEvent.getCourseCode(), gradeEvent.getGrade());
                } else {
                    logger.error("Failed to publish grade event: studentCode={}, courseCode={}, grade={}", 
                        gradeEvent.getStudentCode(), gradeEvent.getCourseCode(), gradeEvent.getGrade(), exception);
                }
            });
        } catch (Exception e) {
            logger.error("Error publishing grade event", e);
            throw new RuntimeException("Failed to publish grade event", e);
        }
    }
}









