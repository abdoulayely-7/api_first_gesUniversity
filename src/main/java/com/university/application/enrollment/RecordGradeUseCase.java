package com.university.application.enrollment;

import com.university.domain.enrollment.Enrollment;
import com.university.infrastructure.persistence.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RecordGradeUseCase {
    private final EnrollmentRepository enrollmentRepository;

    public RecordGradeUseCase(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    public void execute(String enrollmentId, Double score, String feedback) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));

        Enrollment updatedEnrollment = enrollment.recordGrade(score, feedback);
        enrollmentRepository.save(updatedEnrollment);
    }
}
