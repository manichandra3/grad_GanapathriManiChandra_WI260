package org.example.restapi_student.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//RegNo
//RollNo
//Name
//Standard
//School
//Gender
//Percentage
@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    private Long registrationNumber;
    private String name;
    private int Standard;
    private String school;
    private String gender;
    private double percentage;
}
