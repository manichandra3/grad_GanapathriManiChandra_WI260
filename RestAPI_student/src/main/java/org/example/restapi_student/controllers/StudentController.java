package org.example.restapi_student.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.restapi_student.models.Student;
import org.example.restapi_student.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RestController
@RequestMapping("/students")
@Tag(name = "Student Controller", description = "Student management APIs")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Operation(summary = "Find all students")
    @GetMapping
    public List<Student> findAll() {
        return studentService.findAll();
    }

    @GetMapping("/{regNo}")
    public Student findByRegNo(@PathVariable String regNo) {
        return studentService.findByRegNo(regNo);
    }

    @PostMapping
    public Student saveStudent(@RequestBody Student student) {
        return studentService.saveStudent(student);
    }

    @PutMapping("/{regNo}")
    public Student updateStudent(@PathVariable String regNo, @RequestBody Student student) {
        return studentService.updateStudent(regNo, student);
    }

    @PatchMapping("/{regNo}")
    public Student patchStudent(@PathVariable String regNo, @RequestBody java.util.Map<String, Object> updates) {
        return studentService.patchStudent(regNo, updates);
    }

    @DeleteMapping("/{regNo}")
    public void deleteStudent(@PathVariable String regNo) {
        studentService.deleteStudent(regNo);
    }

    @GetMapping("/school")
    public List<Student> findBySchool(@RequestParam(name = "name") String school) {
        return studentService.findBySchool(school);
    }

    @GetMapping("/school/count")
    public long countBySchool(@RequestParam(name = "name") String school) {
        return studentService.countBySchool(school);
    }

    @GetMapping("/school/standard/count")
    public long countByStandard(@RequestParam(name = "class") int standard) {
        return studentService.countByStandard(standard);
    }

    @GetMapping("/result")
    public List<Student> findByPassStatus(@RequestParam(name = "pass") boolean pass) {
        if (pass) {
            return studentService.findByPercentageGreaterThanEqualOrderByPercentageDesc(40.0);
        } else {
            return studentService.findByPercentageLessThanOrderByPercentageDesc(40.0);
        }
    }

    @GetMapping("/strength")
    public long countByGenderAndStandard(@RequestParam(name = "gender") String gender,
            @RequestParam(name = "standard") int standard) {
        return studentService.countByGenderAndStandard(gender, standard);
    }

}