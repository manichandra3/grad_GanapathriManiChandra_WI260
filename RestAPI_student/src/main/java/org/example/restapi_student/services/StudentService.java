package org.example.restapi_student.services;

import org.example.restapi_student.models.Student;

import java.util.List;
import java.util.Map;

public interface StudentService {

    List<Student> findAll();
    Student findByRegNo(String regNo);
    Student saveStudent(Student student);
    Student updateStudent(String regNo, Student student); // For PUT
    Student patchStudent(String regNo, Map<String, Object> updates); // For PATCH
    void deleteStudent(String regNo);

    List<Student> findBySchool(String school);
    long countBySchool(String school);
    long countByStandard(int standard);
    long countByGenderAndStandard(String gender, int standard);

    // pass=true
    List<Student> findByPercentageGreaterThanEqualOrderByPercentageDesc(double percentage);
    // pass=false
    List<Student> findByPercentageLessThanOrderByPercentageDesc(double percentage);
}