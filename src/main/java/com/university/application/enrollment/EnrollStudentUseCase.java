package com.university.application.enrollment;

import com.university.domain.course.Course;
import com.university.domain.enrollment.Enrollment;
import com.university.infrastructure.persistence.CourseRepository;
import com.university.infrastructure.persistence.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class EnrollStudentUseCase {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public EnrollStudentUseCase(EnrollmentRepository enrollmentRepository, 
                                CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    public Enrollment execute(String studentNumber, String courseId, String academicYear) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        if (!course.acceptsEnrollments()) {
            throw new IllegalStateException("Cannot enroll: course is " + course.getStatus());
        }

        Optional<Enrollment> existing = enrollmentRepository
            .findByStudentNumberAndCourseIdAndAcademicYear(studentNumber, courseId, academicYear);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Student already enrolled");
        }

        Enrollment enrollment = Enrollment.create(studentNumber, courseId, academicYear);
        Course updatedCourse = course.addEnrollment();

        enrollmentRepository.save(enrollment);
        courseRepository.save(updatedCourse);

        return enrollment;
    }
}
