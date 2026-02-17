package org.example.springmvc_student.repositories.nosql;

import org.example.springmvc_student.models.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentNoSqlRepository extends MongoRepository<Student, Integer> {
}
