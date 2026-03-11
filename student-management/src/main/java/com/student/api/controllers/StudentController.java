package com.student.api.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.student.api.models.Student;
import com.student.api.services.StudentService;

@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {
        org.springframework.web.bind.annotation.RequestMethod.GET,
        org.springframework.web.bind.annotation.RequestMethod.POST,
        org.springframework.web.bind.annotation.RequestMethod.PUT,
        org.springframework.web.bind.annotation.RequestMethod.PATCH,
        org.springframework.web.bind.annotation.RequestMethod.DELETE
})
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{regNo}")
    public ResponseEntity<Optional<Student>> getStudent(@PathVariable int regNo) {
        return studentService.getStudent(regNo);
    }

    @PostMapping
    public ResponseEntity<String> addStudent(@RequestBody Student student) {
        return studentService.addStudent(student);
    }

    @PutMapping("/{regNo}")
    public ResponseEntity<String> updateStudent(@PathVariable int regNo, @RequestBody Student student) {
        return studentService.updateStudent(regNo, student);
    }

    @PatchMapping("/{regNo}")
    public ResponseEntity<String> patchStudent(@PathVariable int regNo, @RequestBody Map<String, Object> updates) {
        return studentService.patchStudent(regNo, updates);
    }

    @DeleteMapping("/{regNo}")
    public ResponseEntity<String> deleteStudent(@PathVariable int regNo) {
        return studentService.deleteStudent(regNo);
    }

    @GetMapping("/school")
    public ResponseEntity<List<Student>> getStudentsBySchool(@RequestParam String name) {
        return studentService.getStudentsBySchool(name);
    }

    @GetMapping("/school/count")
    public ResponseEntity<Long> getSchoolCount(@RequestParam String name) {
        return studentService.getSchoolCount(name);
    }

    @GetMapping("/school/standard/count")
    public ResponseEntity<Long> getStandardCount(@RequestParam("class") int standard) {
        return studentService.getStandardCount(standard);
    }

    @GetMapping("/result")
    public ResponseEntity<List<Student>> getStudentsByResult(@RequestParam boolean pass) {
        return studentService.getStudentsByResult(pass);
    }

    @GetMapping("/strength")
    public ResponseEntity<Long> getStrength(@RequestParam String gender, @RequestParam int standard) {
        return studentService.getStrength(gender, standard);
    }
}
