package com.university.domain.enrollment;

import java.util.Objects;

public final class EnrollmentId {
    private final String value;

    private EnrollmentId(String value) {
        this.value = Objects.requireNonNull(value, "EnrollmentId cannot be null");
    }

    public static EnrollmentId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("EnrollmentId cannot be blank");
        }
        return new EnrollmentId(value);
    }

    public static EnrollmentId generate() {
        return of("ENR-" + System.currentTimeMillis() + "-" + 
                  String.format("%04d", (int)(Math.random() * 10000)));
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnrollmentId that = (EnrollmentId) o;
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
