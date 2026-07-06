package com.university.domain.shared;

import java.util.Objects;

public final class CourseId {
    private final String value;

    private CourseId(String value) {
        this.value = Objects.requireNonNull(value, "CourseId cannot be null");
    }

    public static CourseId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CourseId cannot be blank");
        }
        return new CourseId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseId courseId = (CourseId) o;
        return value.equals(courseId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
