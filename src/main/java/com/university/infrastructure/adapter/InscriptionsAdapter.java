package com.university.infrastructure.adapter;

import com.university.application.enrollment.EnrollStudentUseCase;
import com.university.application.enrollment.RecordGradeUseCase;
import com.university.domain.enrollment.Enrollment;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Inscriptions", description = "Gestion des inscriptions aux cours")
public class InscriptionsAdapter {
    private final EnrollStudentUseCase enrollStudentUseCase;
    private final RecordGradeUseCase recordGradeUseCase;

    public InscriptionsAdapter(EnrollStudentUseCase enrollStudentUseCase, 
                               RecordGradeUseCase recordGradeUseCase) {
        this.enrollStudentUseCase = enrollStudentUseCase;
        this.recordGradeUseCase = recordGradeUseCase;
    }

    /**
     * POST /api/v1/enrollments
     * Implémente le contrat OpenAPI pour inscrire un étudiant.
     */
    public ResponseEntity<?> enrollStudent(@Valid Map<String, String> request) {
        String studentNumber = request.get("studentNumber");
        String courseId = request.get("courseId");
        String academicYear = request.get("academicYear");

        Enrollment enrollment = enrollStudentUseCase.execute(studentNumber, courseId, academicYear);
        
        Map<String, Object> response = new HashMap<>();
        response.put("enrollmentId", enrollment.getEnrollmentId());
        response.put("studentNumber", enrollment.getStudentNumber());
        response.put("courseId", enrollment.getCourseId());
        response.put("confirmationDate", enrollment.getConfirmationDate());
        response.put("ticketNumber", enrollment.getTicketNumber());
        response.put("academicYear", enrollment.getAcademicYear());
        response.put("status", enrollment.getStatus().getValue());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/v1/enrollments/{enrollmentId}/grades
     * Implémente le contrat OpenAPI pour enregistrer une note.
     */
    public ResponseEntity<Void> updateGrade(String enrollmentId, Map<String, Object> request) {
        Double score = Double.parseDouble(request.get("score").toString());
        String feedback = (String) request.get("feedback");

        recordGradeUseCase.execute(enrollmentId, score, feedback);
        
        return ResponseEntity.noContent().build();
    }
}
