package com.university.infrastructure.adapter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/enrollments")
public class EnrollmentController {
    private final InscriptionsAdapter inscriptionsAdapter;

    public EnrollmentController(InscriptionsAdapter inscriptionsAdapter) {
        this.inscriptionsAdapter = inscriptionsAdapter;
    }

    @PostMapping
    public ResponseEntity<?> enrollStudent(@RequestBody Map<String, String> request) {
        return inscriptionsAdapter.enrollStudent(request);
    }

    @PutMapping("/{enrollmentId}/grades")
    public ResponseEntity<Void> updateGrade(@PathVariable String enrollmentId, 
                                             @RequestBody Map<String, Object> request) {
        return inscriptionsAdapter.updateGrade(enrollmentId, request);
    }
}
