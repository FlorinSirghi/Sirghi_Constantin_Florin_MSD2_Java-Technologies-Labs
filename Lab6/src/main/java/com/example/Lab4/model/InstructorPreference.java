package com.example.Lab4.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "instructor_preferences",
        uniqueConstraints = @UniqueConstraint(name = "uq_instructor_course_compulsory", 
                columnNames = {"instructor_id", "optional_course_id", "compulsory_course_abbr"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorPreference {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "optional_course_id", nullable = false)
    private Course optionalCourse;
    
    @Column(name = "compulsory_course_abbr", nullable = false)
    private String compulsoryCourseAbbr;
    
    @Column(nullable = false)
    private Double percentage; // weight percentage (0.0 to 100.0)
}




