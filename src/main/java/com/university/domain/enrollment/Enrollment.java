package com.university.domain.enrollment;

import com.university.domain.shared.AcademicYear;
import com.university.domain.shared.CourseId;
import com.university.domain.shared.EnrollmentStatus;
import com.university.domain.shared.StudentNumber;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"studentNumber", "courseId", "academicYear"})
})
public class Enrollment {
    @Id
    private String enrollmentId;

    private String studentNumber;
    private String courseId;
    
    @Column(name = "academic_year")
    private String academicYear;
    
    private OffsetDateTime confirmationDate;
    private String ticketNumber;
    
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    private Double score;
    private String feedback;

    protected Enrollment() {}

    private Enrollment(EnrollmentId enrollmentId, StudentNumber studentNumber, CourseId courseId,
                      AcademicYear academicYear, OffsetDateTime confirmationDate, TicketNumber ticketNumber) {
        this.enrollmentId = enrollmentId.getValue();
        this.studentNumber = studentNumber.getValue();
        this.courseId = courseId.getValue();
        this.academicYear = academicYear.getValue();
        this.confirmationDate = confirmationDate;
        this.ticketNumber = ticketNumber.getValue();
        this.status = EnrollmentStatus.CONFIRMED;
        this.score = null;
        this.feedback = null;
    }

    public static Enrollment create(String studentNumber, String courseId, String academicYear) {
        return new Enrollment(
            EnrollmentId.generate(),
            StudentNumber.of(studentNumber),
            CourseId.of(courseId),
            AcademicYear.of(academicYear),
            OffsetDateTime.now(ZoneOffset.UTC),
            TicketNumber.generate()
        );
    }

    public String getEnrollmentId() { return enrollmentId; }
    public String getStudentNumber() { return studentNumber; }
    public String getCourseId() { return courseId; }
    public String getAcademicYear() { return academicYear; }
    public OffsetDateTime getConfirmationDate() { return confirmationDate; }
    public String getTicketNumber() { return ticketNumber; }
    public EnrollmentStatus getStatus() { return status; }
    public Double getScore() { return score; }
    public String getFeedback() { return feedback; }

    public Enrollment recordGrade(Double gradeScore, String gradeFeedback) {
        Grade grade = Grade.of(gradeScore, gradeFeedback);
        
        Enrollment updated = new Enrollment(
            EnrollmentId.of(this.enrollmentId),
            StudentNumber.of(this.studentNumber),
            CourseId.of(this.courseId),
            AcademicYear.of(this.academicYear),
            this.confirmationDate,
            new TicketNumber(this.ticketNumber)
        );
        updated.status = this.status;
        updated.score = grade.getScore();
        updated.feedback = grade.getFeedback();
        return updated;
    }

    public Enrollment cancel() {
        if (status == EnrollmentStatus.CANCELLED) {
            throw new IllegalStateException("Enrollment already cancelled");
        }
        
        Enrollment updated = new Enrollment(
            EnrollmentId.of(this.enrollmentId),
            StudentNumber.of(this.studentNumber),
            CourseId.of(this.courseId),
            AcademicYear.of(this.academicYear),
            this.confirmationDate,
            new TicketNumber(this.ticketNumber)
        );
        updated.status = EnrollmentStatus.CANCELLED;
        updated.score = this.score;
        updated.feedback = this.feedback;
        return updated;
    }

    public boolean isConfirmed() {
        return status == EnrollmentStatus.CONFIRMED;
    }

    public boolean hasGrade() {
        return score != null;
    }
}
