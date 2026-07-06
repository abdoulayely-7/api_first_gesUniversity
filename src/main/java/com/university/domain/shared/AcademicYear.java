package com.university.domain.shared;

import java.util.Objects;
import java.util.regex.Pattern;

public final class AcademicYear {
    private static final Pattern PATTERN = Pattern.compile("^\\d{4}-\\d{4}$");
    private final String value;

    private AcademicYear(String value) {
        this.value = Objects.requireNonNull(value, "AcademicYear cannot be null");
    }

    public static AcademicYear of(String value) {
        if (value == null || !PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("AcademicYear must match format YYYY-YYYY");
        }
        return new AcademicYear(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcademicYear that = (AcademicYear) o;
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
