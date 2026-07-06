package com.university;

import com.university.domain.course.Course;
import com.university.infrastructure.persistence.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UniversityApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniversityApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(CourseRepository courseRepository) {
        return args -> {
            if (courseRepository.count() == 0) {
                courseRepository.save(Course.create("CS-301", "Architecture Logicielle", 6, 
                    "Dr. Jean Dupont", "Licence 3", 30));
                courseRepository.save(Course.create("MATH-201", "Algèbre Avancée", 4, 
                    "Pr. Marie Martin", "Licence 2", 25));
                courseRepository.save(Course.create("CS-302", "Design Patterns", 6, 
                    "Dr. Pierre Bernard", "Licence 3", 30));
                courseRepository.save(Course.create("PHYS-101", "Mécanique Quantique", 5, 
                    "Pr. Michel Rousseau", "Master 1", 20));
                System.out.println("✓ Sample data initialized");
            }
        };
    }
}
