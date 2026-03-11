package com.student.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.student.api.models.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    List<Student> findBySchool(String school);

    long countBySchool(String school);

    long countByStandard(int standard);

    List<Student> findByPercentageGreaterThanEqualOrderByPercentageDesc(double percentage);

    List<Student> findByPercentageLessThanOrderByPercentageDesc(double percentage);

    long countByGenderAndStandard(String gender, int standard);
}
