package fr.yeensight.tarpinfur.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Port (interface) defining the contract for Event persistence.
 *
 * KEY PRINCIPLE - Hexagonal Architecture (Ports & Adapters):
 * - This interface is defined in the DOMAIN layer
 * - The INFRASTRUCTURE layer will implement it (EventRepositoryAdapter)
 * - The domain depends on THIS interface, not on any framework
 * - This enables testing WITHOUT Spring, MongoDB, or any external framework
 *
 * SOLID - Dependency Inversion (DIP):
 * - The domain (high-level) defines the interface
 * - Infrastructure (low-level) implements it
 * - Both depend on the abstraction (this interface), not the other way around
 * - This makes the domain reusable and testable in isolation
 *
 * NO Spring or MongoDB annotations here - this is pure business concern.
 */
public interface EventRepository {

    /**
     * Save or update an event.
     *
     * @param event the event to save (never null)
     * @return the saved event (with ID assigned if it was new)
     */
    Event save(Event event);

    /**
     * Retrieve an event by its ID.
     *
     * @param id the event identifier (never null)
     * @return Optional containing the event, or empty if not found
     */
    Optional<Event> findById(EventId id);

    /**
     * Retrieve all events that occur on or after the given date.
     *
     * @param fromDate the minimum date (never null)
     * @return list of events (empty list if none found, never null)
     */
    List<Event> findByDateGreaterThanOrEqual(Instant fromDate);

    /**
     * Retrieve all events.
     *
     * @return list of all events (empty list if none found, never null)
     */
    List<Event> findAll();

    /**
     * Delete an event by its ID.
     *
     * @param id the event identifier (never null)
     */
    void deleteById(EventId id);
}

