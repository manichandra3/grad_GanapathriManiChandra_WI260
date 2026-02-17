package org.example.springmvc_student.services;

import org.example.springmvc_student.models.Student;

import java.util.List;

public interface StudentService {
    List<Student> findAll();
    Student findById(int id);
    Student findByName(String name);
    Student insert(Student student);
    Student update(Student student);
    void reset();
}
