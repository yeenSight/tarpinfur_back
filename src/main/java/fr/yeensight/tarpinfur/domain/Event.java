package fr.yeensight.tarpinfur.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Domain Entity representing a calendar event.
 *
 * This record encapsulates pure business logic and has NO dependencies on frameworks.
 * - Immutable: thread-safe, no side effects
 * - Complete value object: all required fields are mandatory
 * - No persistence knowledge: this is NOT a JPA entity
 *
 * Design Principles Applied:
 * - Clean Architecture: No framework imports (no Spring, no JPA)
 * - Clean Code: Immutable, clear naming (Ubiquitous Language)
 * - SOLID - SRP: Represents ONLY an event's business domain, not persistence
 *
 * @param id       the unique event identifier (never null)
 * @param title    the event title (never null or empty)
 * @param date     the event date (never null, must be in future)
 * @param location the event location (nullable, can be absent)
 */
public record Event(
    EventId id,
    String title,
    Instant date,
    Optional<String> location
) {

    /**
     * Constructor with validation (compact constructor).
     * Ensures domain invariants are maintained.
     */
    public Event {
        Objects.requireNonNull(id, "Event id must not be null");
        Objects.requireNonNull(title, "Event title must not be null");

        if (title.trim().isEmpty()) {
            throw new IllegalArgumentException("Event title must not be empty");
        }

        Objects.requireNonNull(date, "Event date must not be null");

        if (date.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Event date must not be in the past");
        }

        Objects.requireNonNull(location, "Location Optional must not be null");
    }

    /**
     * Factory method: create an Event with optional location.
     * Recommended way to create Events to ensure consistency.
     */
    public static Event create(EventId id, String title, Instant date, Optional<String> location) {
        return new Event(id, title, date, location);
    }

    /**
     * Factory method: create an Event without location.
     */
    public static Event createWithoutLocation(EventId id, String title, Instant date) {
        return new Event(id, title, date, Optional.empty());
    }
}

