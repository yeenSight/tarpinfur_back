package fr.yeensight.tarpinfur.application.usecase;

import fr.yeensight.tarpinfur.application.dto.EventResponse;
import fr.yeensight.tarpinfur.domain.Event;
import fr.yeensight.tarpinfur.domain.EventRepository;

import java.time.Instant;
import java.util.List;

/**
 * Use Case: Retrieve all events from a given date onwards.
 *
 * Orchestrates fetching events:
 * 1. Receives the query parameters
 * 2. Queries the repository using the domain port
 * 3. Maps results to DTOs
 * 4. Returns the response
 *
 * KEY PRINCIPLES:
 * - No Spring annotations: pure Java orchestration
 * - Depends on EventRepository interface (domain port)
 * - Can be tested without Spring, MongoDB, or any framework
 *
 * SOLID - SRP: Single responsibility = orchestrate event retrieval
 * SOLID - DIP: Depends on EventRepository interface (domain)
 */
public class GetEventsUseCase {

    private final EventRepository eventRepository;

    public GetEventsUseCase(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Execute the use case: retrieve events from a given date onwards.
     *
     * @param fromDate the start date (never null)
     * @return list of events as DTOs (empty list if none found, never null)
     * @throws IllegalArgumentException if fromDate is null
     */
    public List<EventResponse> execute(Instant fromDate) {
        if (fromDate == null) {
            throw new IllegalArgumentException("fromDate must not be null");
        }

        List<Event> events = eventRepository.findByDateGreaterThanOrEqual(fromDate);
        return events.stream().map(this::toDomainResponse).toList();
    }

    /**
     * Execute the use case: retrieve ALL events.
     *
     * @return list of all events as DTOs (empty list if none found, never null)
     */
    public List<EventResponse> executeGetAll() {
        List<Event> events = eventRepository.findAll();
        return events.stream().map(this::toDomainResponse).toList();
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

