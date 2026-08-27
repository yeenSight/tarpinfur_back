package fr.yeensight.tarpinfur.infrastructure.web;

import fr.yeensight.tarpinfur.application.dto.CreateEventRequest;
import fr.yeensight.tarpinfur.application.dto.EventResponse;
import fr.yeensight.tarpinfur.application.usecase.CreateEventUseCase;
import fr.yeensight.tarpinfur.application.usecase.GetEventsUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * REST Controller for Event management.
 *
 * CLEAN ARCHITECTURE PRINCIPLE:
 * - This is a thin adapter between HTTP and application layer
 * - Controllers inject USE CASES (not services, not repositories directly)
 * - Controllers use DTOs for input/output (not domain entities)
 * - All business logic lives in use cases and domain
 * - Controller just translates HTTP ↔ Use Case ↔ DTOs
 *
 * VIOLATIONS FIXED from original code:
 * - Original: exposed EventEntity (JPA entity) as API response
 * - New: uses EventResponse DTO for all API responses
 * - Original: EventService was just a passthrough
 * - New: uses proper use cases with clear responsibilities
 * - Original: EventEntity had framework annotations mixed in
 * - New: clean separation between domain, app, and infra
 *
 * SOLID - SRP:
 * - Controller: translate HTTP → DTOs → Use Cases
 * - Use Cases: orchestrate business logic
 * - Domain: enforce business rules
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final CreateEventUseCase createEventUseCase;
    private final GetEventsUseCase getEventsUseCase;

    public EventController(CreateEventUseCase createEventUseCase, GetEventsUseCase getEventsUseCase) {
        this.createEventUseCase = createEventUseCase;
        this.getEventsUseCase = getEventsUseCase;
    }

    /**
     * POST /api/events - Create a new event.
     *
     * @param request the event creation request (title, date, location)
     * @return 201 Created with the created event
     */
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody CreateEventRequest request) {
        try {
            EventResponse response = createEventUseCase.execute(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * POST /api/events/batch - Create multiple events at once.
     *
     * @param requests list of event creation requests
     * @return 201 Created with all created events
     */
    @PostMapping("/batch")
    public ResponseEntity<List<EventResponse>> createEvents(@RequestBody List<CreateEventRequest> requests) {
        try {
            List<EventResponse> responses = requests.stream()
                .map(createEventUseCase::execute)
                .toList();
            return ResponseEntity.status(HttpStatus.CREATED).body(responses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/events - Retrieve all events.
     *
     * @return 200 OK with list of events
     */
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<EventResponse> events = getEventsUseCase.executeGetAll();
        return ResponseEntity.ok(events);
    }

    /**
     * GET /api/events?from={timestamp} - Retrieve events from a given date onwards.
     *
     * @param from Unix timestamp (Instant) - optional, defaults to now if not provided
     * @return 200 OK with list of events
     */
    @GetMapping(params = "from")
    public ResponseEntity<List<EventResponse>> getEventsSince(@RequestParam long from) {
        try {
            Instant fromDate = Instant.ofEpochSecond(from);
            List<EventResponse> events = getEventsUseCase.execute(fromDate);
            return ResponseEntity.ok(events);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

