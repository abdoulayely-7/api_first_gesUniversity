package com.university.domain.enrollment;

import java.util.Objects;

public final class TicketNumber {
    private final String value;

    public TicketNumber(String value) {
        this.value = Objects.requireNonNull(value, "TicketNumber cannot be null");
    }

    public static TicketNumber generate() {
        return new TicketNumber("TKT-2026-" + String.format("%05d", 
                                 (int)(Math.random() * 100000)));
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketNumber that = (TicketNumber) o;
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
