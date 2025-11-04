package com.example.Lab4.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student extends Person {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Integer year;

    public Student(String code, String name, String email, Integer year) {
        this.setName(name);
        this.setEmail(email);
        this.code = code;
        this.year = year;
    }
}
