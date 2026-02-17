package org.example.springmvc_student.controllers;

import lombok.RequiredArgsConstructor;
import org.example.springmvc_student.models.Student;
import org.example.springmvc_student.services.StudentServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentServiceImpl studentService;

    @GetMapping("/all")
    public ResponseEntity<List<Student>> findAll(){
        return ResponseEntity.ok(studentService.findAll());
    }

    @PostMapping("/insert")
    public ResponseEntity<Student> insert(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.insert(student));
    }
    @DeleteMapping("/reset")
    public ResponseEntity<String> reset() {
        studentService.reset();
        return ResponseEntity.ok("Both Databases have been wiped!");
    }
}
