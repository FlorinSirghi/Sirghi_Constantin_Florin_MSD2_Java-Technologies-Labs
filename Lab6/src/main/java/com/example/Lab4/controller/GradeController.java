package com.example.Lab4.controller;

import com.example.Lab4.model.Grade;
import com.example.Lab4.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @Operation(summary = "Get all grades", description = "Retrieve all grades from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved grades")
    })
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Grade>> getAllGrades() {
        return ResponseEntity.ok(gradeService.getAllGrades());
    }

    @Operation(summary = "Get grades by student code", description = "Retrieve all grades for a specific student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved grades"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @GetMapping(value = "/student/{studentCode}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Grade>> getGradesByStudentCode(@PathVariable String studentCode) {
        List<Grade> grades = gradeService.getGradesByStudentCode(studentCode);
        return ResponseEntity.ok(grades);
    }

    @Operation(summary = "Get grades by course code", description = "Retrieve all grades for a specific course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved grades"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping(value = "/course/{courseCode}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Grade>> getGradesByCourseCode(@PathVariable String courseCode) {
        List<Grade> grades = gradeService.getGradesByCourseCode(courseCode);
        return ResponseEntity.ok(grades);
    }

    @Operation(summary = "Load grades from CSV file", description = "Upload a CSV file with grades (format: studentCode,courseCode,grade)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "CSV file processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file format or content")
    })
    @PostMapping(value = "/upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadGradesFromCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        if (!file.getContentType().equals("text/csv") && !file.getOriginalFilename().endsWith(".csv")) {
            return ResponseEntity.badRequest().body("File must be a CSV file");
        }

        try {
            List<String[]> csvData = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    String[] values = line.split(",");
                    if (values.length >= 3) {
                        csvData.add(values);
                    }
                }
            }

            if (csvData.isEmpty()) {
                return ResponseEntity.badRequest().body("CSV file is empty or has no valid rows");
            }

            gradeService.loadGradesFromCsv(csvData);
            return ResponseEntity.ok("CSV file processed successfully. " + csvData.size() + " rows processed.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing CSV file: " + e.getMessage());
        }
    }
}









