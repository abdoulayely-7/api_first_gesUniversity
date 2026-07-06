package com.university.infrastructure.adapter;

import com.university.application.course.ListCoursesUseCase;
import com.university.domain.course.Course;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Catalogue", description = "Gestion du catalogue des cours")
public class CatalogueAdapter {
    private final ListCoursesUseCase listCoursesUseCase;

    public CatalogueAdapter(ListCoursesUseCase listCoursesUseCase) {
        this.listCoursesUseCase = listCoursesUseCase;
    }

    /**
     * GET /api/v1/courses
     * Implémente le contrat OpenAPI pour lister les cours.
     */
    public ResponseEntity<?> listCourses(String level) {
        List<Course> courses = listCoursesUseCase.execute(level);
        
        Map<String, Object> response = new HashMap<>();
        response.put("courses", courses.stream()
            .map(this::mapCourseToDTO)
            .toList());
        response.put("totalElements", courses.size());
        response.put("currentPage", 0);
        
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> mapCourseToDTO(Course course) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("courseId", course.getCourseId());
        dto.put("title", course.getTitle());
        dto.put("credits", course.getCredits());
        dto.put("professorName", course.getProfessorName());
        dto.put("status", course.getStatus().getValue());
        dto.put("level", course.getLevel().getValue());
        dto.put("maxStudents", course.getMaxStudents());
        dto.put("enrolledCount", course.getEnrolledCount());
        return dto;
    }
}
