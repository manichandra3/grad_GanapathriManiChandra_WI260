package org.example.restapi_student.services;

import org.example.restapi_student.models.Student;
import org.example.restapi_student.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    public Student findByRegNo(String regNo) {
        return studentRepository.findStudentByRegistrationNumber(Long.parseLong(regNo));
    }

    @Override
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(String regNo, Student student) {
        Student existingStudent = studentRepository.findStudentByRegistrationNumber(Long.parseLong(regNo));
        if (existingStudent == null) {
            throw new RuntimeException("Student not found!");
        }
        existingStudent.setName(student.getName());
        existingStudent.setStandard(student.getStandard());
        existingStudent.setSchool(student.getSchool());
        existingStudent.setGender(student.getGender());
        existingStudent.setPercentage(student.getPercentage());
        return studentRepository.save(existingStudent);
    }

    @Override
    public Student patchStudent(String regNo, Map<String, Object> updates) {
        Student student = studentRepository.findStudentByRegistrationNumber(Long.parseLong(regNo));
        if (student == null) {
            throw new RuntimeException("Student not found!");
        }
        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> student.setName((String) value);
                case "standard" -> student.setStandard((int) value);
                case "school" -> student.setSchool((String) value);
                case "gender" -> student.setGender((String) value);
                case "percentage" -> student.setPercentage(((Number) value).doubleValue());
            }
        });
        return studentRepository.save(student);
    }

    @Override
    public void deleteStudent(String regNo) {
        studentRepository.deleteById(Long.parseLong(regNo));
    }

    @Override
    public List<Student> findBySchool(String school) {
        return studentRepository.findBySchool(school);
    }

    @Override
    public long countBySchool(String school) {
        return studentRepository.countBySchool(school);
    }

    @Override
    public long countByStandard(int standard) {
        return studentRepository.countByStandard(standard);
    }

    @Override
    public long countByGenderAndStandard(String gender, int standard) {
        return studentRepository.countByGenderAndStandard(gender, standard);
    }

    @Override
    public List<Student> findByPercentageGreaterThanEqualOrderByPercentageDesc(double percentage) {
        return studentRepository.findByPercentageGreaterThanEqualOrderByPercentageDesc(percentage);
    }

    @Override
    public List<Student> findByPercentageLessThanOrderByPercentageDesc(double percentage) {
        return studentRepository.findByPercentageLessThanOrderByPercentageDesc(percentage);
    }
}
