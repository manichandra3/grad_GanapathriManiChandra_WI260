package org.example.restapi_student.repositories;

import org.example.restapi_student.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Student findStudentByRegistrationNumber(Long registrationNumber);

    // 1. GET /students/school?name=KV
    List<Student> findBySchool(String school);

    // 2. GET /students/school/count?name=DPS
    long countBySchool(String school);

    // 3. GET /students/school/standard/count?class=5
    long countByStandard(int standard);

    // 4. GET /students/result?pass=true/false (Pass >= 40%)
    // For pass=true -> pass 40.0 as the argument
    List<Student> findByPercentageGreaterThanEqualOrderByPercentageDesc(double percentage);

    // For pass=false -> pass 40.0 as the argument
    List<Student> findByPercentageLessThanOrderByPercentageDesc(double percentage);

    // 5. GET /students/strength?gender=MALE&standard=5
    long countByGenderAndStandard(String gender, int standard);

}
