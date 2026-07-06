package com.university.infrastructure.adapter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    private final CatalogueAdapter catalogueAdapter;

    public CourseController(CatalogueAdapter catalogueAdapter) {
        this.catalogueAdapter = catalogueAdapter;
    }

    @GetMapping
    public ResponseEntity<?> listCourses(@RequestParam(required = false) String level) {
        return catalogueAdapter.listCourses(level);
    }
}
