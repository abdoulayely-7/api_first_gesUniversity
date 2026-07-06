package com.university.domain.shared;

import java.util.Objects;

public final class StudentNumber {
    private final String value;

    private StudentNumber(String value) {
        this.value = Objects.requireNonNull(value, "StudentNumber cannot be null");
    }

    public static StudentNumber of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("StudentNumber cannot be blank");
        }
        return new StudentNumber(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentNumber that = (StudentNumber) o;
        return value.equals(that.value);
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
