package org.example.springmvc_student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.example.springmvc_student.repositories.sql")
@EnableMongoRepositories(basePackages = "org.example.springmvc_student.repositories.nosql")
public class SpringMvcStudentApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringMvcStudentApplication.class, args);
    }
}
