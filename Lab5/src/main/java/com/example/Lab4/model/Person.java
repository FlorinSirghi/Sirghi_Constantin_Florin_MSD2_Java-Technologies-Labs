package com.example.Lab4.model;


import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.NoArgsConstructor;


@MappedSuperclass
@Data
@NoArgsConstructor
public abstract class Person {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String name;


    @Column(nullable = false, unique = true)
    private String email;
}