package com.university.domain.course;

import com.university.domain.shared.CourseId;
import com.university.domain.shared.CourseLevel;
import com.university.domain.shared.CourseStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    private String courseId;

    private String title;
    private Integer credits;
    private String professorName;
    
    @Enumerated(EnumType.STRING)
    private CourseStatus status;
    
    @Enumerated(EnumType.STRING)
    private CourseLevel level;

    private Integer maxStudents;
    private Integer enrolledCount;

    // Constructeur protégé pour JPA
    protected Course() {}

    // Constructeur privé - factory method
    private Course(CourseId courseId, String title, Integer credits, String professorName, 
                   CourseLevel level, Integer maxStudents) {
        this.courseId = courseId.getValue();
        this.title = title;
        this.credits = credits;
        this.professorName = professorName;
        this.level = level;
        this.status = CourseStatus.OUVERT;
        this.maxStudents = maxStudents;
        this.enrolledCount = 0;
    }

    // Factory method pour créer un cours
    public static Course create(String courseId, String title, Integer credits, 
                                String professorName, String level, Integer maxStudents) {
        return new Course(
            CourseId.of(courseId),
            title,
            credits,
            professorName,
            CourseLevel.fromValue(level),
            maxStudents
        );
    }

    // ========== GETTERS - READ ONLY ==========
    public String getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public Integer getCredits() {
        return credits;
    }

    public String getProfessorName() {
        return professorName;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public CourseLevel getLevel() {
        return level;
    }

    public Integer getMaxStudents() {
        return maxStudents;
    }

    public Integer getEnrolledCount() {
        return enrolledCount;
    }

    // ========== BUSINESS LOGIC METHODS - IMMUTABLE ==========

    /**
     * Vérifie si le cours est plein.
     */
    public boolean isFull() {
        return enrolledCount >= maxStudents;
    }

    /**
     * Vérifie si le cours accepte les inscriptions.
     */
    public boolean acceptsEnrollments() {
        return status == CourseStatus.OUVERT && !isFull();
    }

    /**
     * Ajoute un étudiant inscrit (appelé par l'agrégat Enrollment).
     * Retourne un nouveau Course si succès (immutable pattern).
     */
    public Course addEnrollment() {
        if (!acceptsEnrollments()) {
            throw new IllegalStateException(
                "Cannot enroll: course is " + status + (isFull() ? " and full" : ""));
        }
        
        Course updated = new Course(
            CourseId.of(this.courseId),
            this.title,
            this.credits,
            this.professorName,
            this.level,
            this.maxStudents
        );
        updated.status = this.status;
        updated.enrolledCount = this.enrolledCount + 1;
        return updated;
    }

    /**
     * Ferme le cours.
     */
    public Course close() {
        Course updated = new Course(
            CourseId.of(this.courseId),
            this.title,
            this.credits,
            this.professorName,
            this.level,
            this.maxStudents
        );
        updated.status = CourseStatus.FERME;
        updated.enrolledCount = this.enrolledCount;
        return updated;
    }

    /**
     * Marque le cours comme complet.
     */
    public Course markFull() {
        if (!isFull()) {
            throw new IllegalStateException("Course is not full yet");
        }
        Course updated = new Course(
            CourseId.of(this.courseId),
            this.title,
            this.credits,
            this.professorName,
            this.level,
            this.maxStudents
        );
        updated.status = CourseStatus.COMPLET;
        updated.enrolledCount = this.enrolledCount;
        return updated;
    }
}
