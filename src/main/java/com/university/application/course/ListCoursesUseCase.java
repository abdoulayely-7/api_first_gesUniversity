package com.university.application.course;

import com.university.domain.course.Course;
import com.university.domain.shared.CourseLevel;
import com.university.infrastructure.persistence.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListCoursesUseCase {
    private final CourseRepository courseRepository;

    public ListCoursesUseCase(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> execute(String level) {
        if (level != null && !level.isBlank()) {
            return courseRepository.findByLevel(CourseLevel.fromValue(level));
        }
        return courseRepository.findAll();
    }
}
