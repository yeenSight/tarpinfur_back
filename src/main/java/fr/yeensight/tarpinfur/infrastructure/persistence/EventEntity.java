package fr.yeensight.tarpinfur.infrastructure.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Optional;

/**
 * JPA/MongoDB Entity for Event persistence.
 *
 * KEY PRINCIPLE - Clear Separation of Concerns:
 * - This class is ONLY for persistence (MongoDB)
 * - It has Spring/MongoDB annotations because it's in INFRASTRUCTURE layer
 * - It's NOT the domain Event entity
 * - Mapping between this and domain.Event is explicit (in EventMapper)
 *
 * VIOLATION AVOIDED from original code:
 * - Original: EventEntity was mixing persistence + business logic + framework deps
 * - New: EventEntity is ONLY a persistence container, zero business logic
 *
 * CLEAN CODE:
 * - Uses Java records for immutability (if Instant is suitable)
 * - Or mutable getter/setter for JPA if needed
 * - Here: kept mutable for JPA compatibility, but with clear separation
 */
@Document(collection = "events")
public class EventEntity {
    @Id
    private String id;
    private String title;
    private Instant date;
    private String location; // nullable

    // Empty constructor required by Spring Data MongoDB
    public EventEntity() {
    }

    public EventEntity(String id, String title, Instant date, String location) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.location = location;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Instant getDate() {
        return date;
    }

    public Optional<String> getLocation() {
        return Optional.ofNullable(location);
    }

    // Setters (for JPA hydration)
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

