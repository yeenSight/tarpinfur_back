package fr.yeensight.tarpinfur.infrastructure.persistence;

import fr.yeensight.tarpinfur.domain.Event;
import fr.yeensight.tarpinfur.domain.EventId;

/**
 * Mapper for converting between Domain Event and JPA EventEntity.
 *
 * KEY PRINCIPLE - Explicit Boundary Crossing:
 * - Maps Domain objects (pure) → JPA objects (persistence)
 * - Maps JPA objects → Domain objects (pure business logic)
 * - All conversions are explicit, testable, and in one place
 * - No hidden magic or implicit conversions
 *
 * SOLID - SRP: Single responsibility = mapping between layers
 * CLEAN CODE: Mapping logic is centralized and clear
 *
 * This pattern prevents the "domain object contamination" where frameworks
 * annotations leak into business logic.
 */
public class EventMapper {

    /**
     * Convert Domain Event to JPA EventEntity for persistence.
     *
     * @param event domain event (never null)
     * @return JPA entity ready for MongoDB
     */
    public static EventEntity toEntity(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event must not be null");
        }

        return new EventEntity(
            event.id().value(),
            event.title(),
            event.date(),
            event.location().orElse(null) // nullable in JPA
        );
    }

    /**
     * Convert JPA EventEntity to Domain Event.
     * This is the "entry point" for data from persistence.
     *
     * @param entity JPA entity from database (never null)
     * @return domain Event with all business rules applied
     */
    public static Event toDomain(EventEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("EventEntity must not be null");
        }

        EventId id = new EventId(entity.getId());
        return Event.create(
            id,
            entity.getTitle(),
            entity.getDate(),
            entity.getLocation()
        );
    }
}

