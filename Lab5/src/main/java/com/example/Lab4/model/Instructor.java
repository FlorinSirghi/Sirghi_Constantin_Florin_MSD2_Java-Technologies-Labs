package com.example.Lab4.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "instructors")
@Data
@NoArgsConstructor
public class Instructor extends Person {
    public Instructor(String name, String email) {
        this.setName(name);
        this.setEmail(email);
    }
}