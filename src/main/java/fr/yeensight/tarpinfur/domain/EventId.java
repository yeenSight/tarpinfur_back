package fr.yeensight.tarpinfur.domain;

import java.util.Objects;

/**
 * Value Object representing a unique Event identifier.
 * Encapsulates the ID string and enforces its type safety.
 * Immutable and can be safely shared.
 *
 * SOLID - Single Responsibility: Manages identity representation only.
 */
public final class EventId {
    private final String value;

    /**
     * Create a new EventId.
     *
     * @param value the unique identifier (must not be null or empty)
     * @throws IllegalArgumentException if value is null or empty
     */
    public EventId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("EventId value must not be null or empty");
        }
        this.value = value;
    }

    /**
     * Get the raw string value of this ID.
     *
     * @return the ID value
     */
    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventId eventId)) return false;
        return Objects.equals(value, eventId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "EventId{" + value + '}';
    }
}

