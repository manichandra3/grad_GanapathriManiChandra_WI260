package org.example.springmvc_student.repositories.sql;

import org.example.springmvc_student.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentSqlRepository extends JpaRepository<Student, Integer> {
    Student findByName(String name);
}
