package com.example.Lab4.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "id")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student extends UserAccount {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Integer year;

    public Student(String code, String fullName, String email, String password, Integer year) {
        this.setFullName(fullName);
        this.setEmail(email);
        this.setPassword(password);
        this.addRole(UserRole.STUDENT);
        this.code = code;
        this.year = year;
    }
}
