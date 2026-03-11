package com.student.api.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "students")
public class Student {
    @Id
    private int registrationNumber;
    private int rollNumber;
    private String name;
    private int standard;
    private String school;
    private String gender;
    private double percentage;
}
