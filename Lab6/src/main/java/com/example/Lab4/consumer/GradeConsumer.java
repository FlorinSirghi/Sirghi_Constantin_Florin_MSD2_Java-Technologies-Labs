package com.example.Lab4.consumer;

import com.example.Lab4.model.GradeEvent;
import com.example.Lab4.service.GradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class GradeConsumer {

    private static final Logger logger = LoggerFactory.getLogger(GradeConsumer.class);

    @Autowired
    private GradeService gradeService;

    @KafkaListener(topics = "${kafka.topic.grades:grades-topic}", groupId = "${spring.kafka.consumer.group-id:prefschedule-group}")
    public void consumeGradeEvent(
            @Payload GradeEvent gradeEvent,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            logger.info("Received grade event - Student Code: {}, Course Code: {}, Grade: {}", 
                gradeEvent.getStudentCode(), gradeEvent.getCourseCode(), gradeEvent.getGrade());
            
            System.out.println("=== Grade Event Received ===");
            System.out.println("Student Code: " + gradeEvent.getStudentCode());
            System.out.println("Course Code: " + gradeEvent.getCourseCode());
            System.out.println("Grade: " + gradeEvent.getGrade());
            System.out.println("Topic: " + topic + ", Partition: " + partition + ", Offset: " + offset);
            System.out.println("===========================");

            gradeService.saveGrade(
                gradeEvent.getStudentCode(), 
                gradeEvent.getCourseCode(), 
                gradeEvent.getGrade()
            );

            acknowledgment.acknowledge();
            logger.info("Successfully processed grade event for student: {}, course: {}", 
                gradeEvent.getStudentCode(), gradeEvent.getCourseCode());

        } catch (Exception e) {
            logger.error("Error processing grade event: studentCode={}, courseCode={}, grade={}", 
                gradeEvent.getStudentCode(), gradeEvent.getCourseCode(), gradeEvent.getGrade(), e);
            // Don't acknowledge - message will be retried or sent to DLQ
            throw new RuntimeException("Failed to process grade event", e);
        }
    }
}









