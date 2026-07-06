package com.university.domain.enrollment;

import java.util.Objects;

public final class Grade {
    private final Double score;
    private final String feedback;

    private Grade(Double score, String feedback) {
        if (score < 0 || score > 20) {
            throw new IllegalArgumentException("Score must be between 0 and 20");
        }
        this.score = score;
        this.feedback = feedback;
    }

    public static Grade of(Double score, String feedback) {
        return new Grade(score, feedback);
    }

    public static Grade of(Double score) {
        return new Grade(score, null);
    }

    public Double getScore() {
        return score;
    }

    public String getFeedback() {
        return feedback;
    }

    public boolean isGraded() {
        return score != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grade grade = (Grade) o;
        return Objects.equals(score, grade.score) && Objects.equals(feedback, grade.feedback);
    }

    @Override
    public int hashCode() {
        return Objects.hash(score, feedback);
    }
}
