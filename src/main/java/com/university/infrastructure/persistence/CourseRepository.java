package com.university.infrastructure.persistence;

import com.university.domain.course.Course;
import com.university.domain.shared.CourseLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    List<Course> findByLevel(CourseLevel level);
}
