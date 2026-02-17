package org.example.springmvc_student.services;

import org.example.springmvc_student.models.Student;
import org.example.springmvc_student.repositories.nosql.StudentNoSqlRepository;
import org.example.springmvc_student.repositories.sql.StudentSqlRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentSqlRepository sqlRepo;
    private final StudentNoSqlRepository mongoRepo;

    @Override
    @Transactional
    public Student insert(Student student) {
        Student savedSql = sqlRepo.save(student);
        return mongoRepo.save(savedSql);
    }

    @Override
    public List<Student> findAll() {
        return sqlRepo.findAll();
    }

    @Override
    public Student findById(int id) {
        return sqlRepo.findById(id).orElse(null);
    }

    @Override
    public Student findByName(String name) {
        return sqlRepo.findByName(name);
    }

    @Override
    @Transactional
    public Student update(Student student) {
        sqlRepo.save(student);
        return mongoRepo.save(student);
    }

    @Override
    @Transactional
    public void reset() {
        mongoRepo.deleteAll();
        sqlRepo.deleteAll();
    }
}