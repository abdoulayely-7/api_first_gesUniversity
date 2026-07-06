package com.university.infrastructure.persistence;

import com.university.domain.enrollment.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {
    List<Enrollment> findByStudentNumber(String studentNumber);
    List<Enrollment> findByCourseId(String courseId);
    Optional<Enrollment> findByStudentNumberAndCourseIdAndAcademicYear(
        String studentNumber, String courseId, String academicYear);
}
