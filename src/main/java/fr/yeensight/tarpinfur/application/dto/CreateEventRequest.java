package fr.yeensight.tarpinfur.application.dto;

import java.time.Instant;
import java.util.Optional;

/**
 * DTO for creating a new Event (Request).
 *
 * This record travels between the web layer and use case layer.
 * It's NOT a domain entity - it's a boundary object.
 *
 * SOLID - SRP: Represents only the input data needed to create an event.
 */
public record CreateEventRequest(
    String title,
    Instant date,
    Optional<String> location
) {
    public CreateEventRequest {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date is required");
        }
        if (location == null) {
            throw new IllegalArgumentException("Location Optional must not be null");
        }
    }
}

