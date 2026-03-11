package com.student.api.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.student.api.models.Student;

public interface StudentService {
    ResponseEntity<List<Student>> getAllStudents();
    ResponseEntity<String> addStudent(Student student);
    ResponseEntity<Optional<Student>> getStudent(int registrationNumber);
    ResponseEntity<String> updateStudent(int registrationNumber, Student student);
    ResponseEntity<String> patchStudent(int registrationNumber, Map<String, Object> updates);
    ResponseEntity<String> deleteStudent(int registrationNumber);
    ResponseEntity<List<Student>> getStudentsBySchool(String schoolName);
    ResponseEntity<Long> getSchoolCount(String schoolName);
    ResponseEntity<Long> getStandardCount(int standard);
    ResponseEntity<List<Student>> getStudentsByResult(boolean pass);
    ResponseEntity<Long> getStrength(String gender, int standard);
}
