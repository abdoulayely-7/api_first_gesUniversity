package com.university.domain.shared;

public enum CourseLevel {
    LICENCE_1("Licence 1"),
    LICENCE_2("Licence 2"),
    LICENCE_3("Licence 3"),
    MASTER_1("Master 1"),
    MASTER_2("Master 2");

    private final String value;

    CourseLevel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CourseLevel fromValue(String value) {
        for (CourseLevel level : CourseLevel.values()) {
            if (level.value.equals(value)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid course level: " + value);
    }
}
