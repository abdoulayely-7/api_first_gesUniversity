package com.university;

import com.university.domain.course.Course;
import com.university.infrastructure.persistence.CourseRepository;
import com.university.infrastructure.persistence.EnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("API First - DDD Integration Tests")
public class UniversityApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        enrollmentRepository.deleteAll();

        courseRepository.save(Course.create("CS-301", "Architecture Logicielle", 6, 
            "Dr. Jean Dupont", "Licence 3", 2));
        courseRepository.save(Course.create("MATH-201", "Algèbre Avancée", 4, 
            "Pr. Marie Martin", "Licence 3", 30));
    }

    @Test
    @DisplayName("GET /api/v1/courses - Récupérer tous les cours")
    void testListAllCourses() throws Exception {
        mockMvc.perform(get("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.courses", hasSize(2)))
            .andExpect(jsonPath("$.courses[0].courseId", notNullValue()))
            .andExpect(jsonPath("$.courses[0].title", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/v1/courses?level=Licence 3 - Filtrer par niveau")
    void testListCoursesByLevel() throws Exception {
        mockMvc.perform(get("/api/v1/courses")
                .param("level", "Licence 3")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.courses", hasSize(2)));
    }

    @Test
    @DisplayName("POST /api/v1/enrollments - Inscrire un étudiant (succès)")
    void testEnrollStudent_Success() throws Exception {
        String enrollmentJson = "{" +
            "\"studentNumber\":\"ETU-2026-045\"," +
            "\"courseId\":\"CS-301\"," +
            "\"academicYear\":\"2025-2026\"" +
            "}";

        mockMvc.perform(post("/api/v1/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enrollmentJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.enrollmentId", notNullValue()))
            .andExpect(jsonPath("$.status", equalTo("CONFIRMED")))
            .andExpect(jsonPath("$.studentNumber", equalTo("ETU-2026-045")))
            .andExpect(jsonPath("$.courseId", equalTo("CS-301")))
            .andExpect(jsonPath("$.ticketNumber", notNullValue()))
            .andExpect(jsonPath("$.confirmationDate", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/enrollments - Données invalides (400)")
    void testEnrollStudent_MissingData() throws Exception {
        String enrollmentJson = "{\"studentNumber\":\"ETU-2026-045\"}";

        mockMvc.perform(post("/api/v1/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enrollmentJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/enrollments - Cours complet (409 Conflict)")
    void testEnrollStudent_CourseFull() throws Exception {
        // Inscrire 2 étudiants (capacité max = 2)
        mockMvc.perform(post("/api/v1/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentNumber\":\"ETU-001\",\"courseId\":\"CS-301\",\"academicYear\":\"2025-2026\"}"))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentNumber\":\"ETU-002\",\"courseId\":\"CS-301\",\"academicYear\":\"2025-2026\"}"))
            .andExpect(status().isCreated());

        // Essayer d'en inscrire un 3e - doit échouer
        mockMvc.perform(post("/api/v1/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentNumber\":\"ETU-003\",\"courseId\":\"CS-301\",\"academicYear\":\"2025-2026\"}"))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /api/v1/enrollments/{enrollmentId}/grades - Enregistrer une note")
    void testRecordGrade_Success() throws Exception {
        // Inscrire d'abord
        String enrollmentJson = "{\"studentNumber\":\"ETU-2026-045\",\"courseId\":\"MATH-201\",\"academicYear\":\"2025-2026\"}";
        var result = mockMvc.perform(post("/api/v1/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enrollmentJson))
            .andExpect(status().isCreated())
            .andReturn();

        String response = result.getResponse().getContentAsString();
        String enrollmentId = response.contains("ENR-") ? 
            response.substring(response.indexOf("\"enrollmentId\":\"") + 16, 
                              response.indexOf("\",\"studentNumber")) : null;

        // Enregistrer une note
        String gradeJson = "{\"score\":15.5,\"feedback\":\"Excellent travail\"}";
        mockMvc.perform(put("/api/v1/enrollments/" + enrollmentId + "/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gradeJson))
            .andExpect(status().isNoContent());
    }
}
