package com.example.Lab4.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, unique = true)
    private String code;

    private String abbr;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    @ManyToOne
    @JoinColumn(name = "pack_id")
    private Pack pack;

    private Integer groupCount = 1;

    @Column(columnDefinition = "text")
    private String description;

    public Course(String type, String code, String abbr, String name, Instructor instructor, Pack pack, Integer groupCount, String description) {
        this.type = type;
        this.code = code;
        this.abbr = abbr;
        this.name = name;
        this.instructor = instructor;
        this.pack = pack;
        this.groupCount = groupCount;
        this.description = description;
    }
}