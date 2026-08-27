package fr.yeensight.tarpinfur.application.usecase;

import fr.yeensight.tarpinfur.application.dto.CreateEventRequest;
import fr.yeensight.tarpinfur.application.dto.EventResponse;
import fr.yeensight.tarpinfur.domain.Event;
import fr.yeensight.tarpinfur.domain.EventId;
import fr.yeensight.tarpinfur.domain.EventRepository;

import java.util.UUID;

/**
 * Use Case: Create a new Event.
 *
 * Orchestrates the creation of an event:
 * 1. Receives the command (CreateEventRequest)
 * 2. Creates the domain entity (Event with business rules)
 * 3. Persists it using the repository port
 * 4. Returns the result (EventResponse DTO)
 *
 * KEY PRINCIPLES:
 * - No Spring annotations: pure Java orchestration
 * - Depends on EventRepository interface (domain port), not Spring Data
 * - Can be tested without Spring, MongoDB, or any framework
 * - Clear separation: Request DTO → Domain Entity → Response DTO
 *
 * SOLID - SRP: Single responsibility = orchestrate event creation
 * SOLID - DIP: Depends on EventRepository interface (domain), not concrete impl
 */
public class CreateEventUseCase {

    private final EventRepository eventRepository;

    public CreateEventUseCase(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Execute the use case: create a new event.
     *
     * @param request the creation request (never null)
     * @return the created event as a DTO
     * @throws IllegalArgumentException if request is invalid
     */
    public EventResponse execute(CreateEventRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CreateEventRequest must not be null");
        }

        // Create domain entity with business rules validation
        EventId newId = new EventId(UUID.randomUUID().toString());
        Event domainEvent = Event.create(newId, request.title(), request.date(), request.location());

        // Persist using the port (interface)
        Event savedEvent = eventRepository.save(domainEvent);

        // Return as DTO (translate domain back to application boundary)
        return toDomainResponse(savedEvent);
    }

    /**
     * Map domain entity to response DTO.
     */
    private EventResponse toDomainResponse(Event event) {
        return new EventResponse(
            event.id().value(),
            event.title(),
            event.date(),
            event.location()
        );
    }
}

