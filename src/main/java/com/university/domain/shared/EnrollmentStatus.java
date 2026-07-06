package com.university.domain.shared;

public enum EnrollmentStatus {
    CONFIRMED("CONFIRMED"),
    PENDING("PENDING"),
    CANCELLED("CANCELLED");

    private final String value;

    EnrollmentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EnrollmentStatus fromValue(String value) {
        for (EnrollmentStatus status : EnrollmentStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid enrollment status: " + value);
    }
}
