package com.example.Lab4.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "instructors")
@jakarta.persistence.PrimaryKeyJoinColumn(name = "id")
@Data
@NoArgsConstructor
public class Instructor extends UserAccount {

    public Instructor(String fullName, String email, String password) {
        this.setFullName(fullName);
        this.setEmail(email);
        this.setPassword(password);
        this.addRole(UserRole.INSTRUCTOR);
    }
}