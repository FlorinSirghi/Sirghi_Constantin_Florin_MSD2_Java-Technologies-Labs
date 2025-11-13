package com.example.Lab4.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "packs")
@Data
@NoArgsConstructor
public class Pack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer semester;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "pack", cascade = CascadeType.ALL)
    private List<Course> courses;

    public Pack(Integer year, Integer semester, String name) {
        this.year = year;
        this.semester = semester;
        this.name = name;
    }
}