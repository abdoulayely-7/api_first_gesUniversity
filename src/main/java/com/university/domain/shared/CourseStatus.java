package com.university.domain.shared;

public enum CourseStatus {
    OUVERT("OUVERT"),
    COMPLET("COMPLET"),
    FERME("FERME");

    private final String value;

    CourseStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CourseStatus fromValue(String value) {
        for (CourseStatus status : CourseStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid course status: " + value);
    }
}
