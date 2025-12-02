package com.example.QuickGrade.controller;

import com.example.QuickGrade.model.GradeEvent;
import com.example.QuickGrade.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @PostMapping
    public ResponseEntity<String> publishGrade(@RequestBody GradeEvent gradeEvent) {
        try {
            gradeService.publishGrade(gradeEvent);
            return ResponseEntity.ok("Grade event published successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to publish grade event: " + e.getMessage());
        }
    }
}









