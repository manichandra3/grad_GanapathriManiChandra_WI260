package com.student.api.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.student.api.models.Student;
import com.student.api.repository.StudentRepository;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentRepository.findAll());
    }

    @Override
    public ResponseEntity<Student> getStudent(int registrationNumber) {
        Optional<Student> student = studentRepository.findById(registrationNumber);
        if (student.isPresent()) {
            return ResponseEntity.ok(student.get());
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<String> addStudent(Student student) {
        studentRepository.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body("Student added successfully");
    }

    @Override
    public ResponseEntity<String> updateStudent(int registrationNumber, Student student) {
        Optional<Student> existing = studentRepository.findById(registrationNumber);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        student.setRegistrationNumber(registrationNumber);
        studentRepository.save(student);
        return ResponseEntity.ok("Student updated successfully");
    }

    @Override
    public ResponseEntity<String> patchStudent(int registrationNumber, Map<String, Object> updates) {
        Optional<Student> existing = studentRepository.findById(registrationNumber);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Student student = existing.get();
        updates.forEach((key, value) -> {
            switch (key) {
                case "rollNumber" -> student.setRollNumber(((Number) value).intValue());
                case "name" -> student.setName((String) value);
                case "standard" -> student.setStandard(((Number) value).intValue());
                case "school" -> student.setSchool((String) value);
                case "gender" -> student.setGender((String) value);
                case "percentage" -> student.setPercentage(((Number) value).doubleValue());
            }
        });
        studentRepository.save(student);
        return ResponseEntity.ok("Student patched successfully");
    }

    @Override
    public ResponseEntity<String> deleteStudent(int registrationNumber) {
        if (!studentRepository.existsById(registrationNumber)) {
            return ResponseEntity.notFound().build();
        }
        studentRepository.deleteById(registrationNumber);
        return ResponseEntity.ok("Student deleted successfully");
    }

    @Override
    public ResponseEntity<List<Student>> getStudentsBySchool(String schoolName) {
        return ResponseEntity.ok(studentRepository.findBySchool(schoolName));
    }

    @Override
    public ResponseEntity<Long> getSchoolCount(String schoolName) {
        return ResponseEntity.ok(studentRepository.countBySchool(schoolName));
    }

    @Override
    public ResponseEntity<Long> getStandardCount(int standard) {
        return ResponseEntity.ok(studentRepository.countByStandard(standard));
    }

    @Override
    public ResponseEntity<List<Student>> getStudentsByResult(boolean pass) {
        if (pass) {
            return ResponseEntity.ok(studentRepository.findByPercentageGreaterThanEqualOrderByPercentageDesc(40.0));
        }
        return ResponseEntity.ok(studentRepository.findByPercentageLessThanOrderByPercentageDesc(40.0));
    }

    @Override
    public ResponseEntity<Long> getStrength(String gender, int standard) {
        return ResponseEntity.ok(studentRepository.countByGenderAndStandard(gender, standard));
    }
}
