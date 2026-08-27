package fr.yeensight.tarpinfur.application.dto;

import java.time.Instant;
import java.util.Optional;

/**
 * DTO for returning Event data to the client (Response).
 *
 * Contains only what the API consumer needs to know.
 *
 * SOLID - SRP: Represents only the output data of an event.
 */
public record EventResponse(
    String id,
    String title,
    Instant date,
    Optional<String> location
) {
}

