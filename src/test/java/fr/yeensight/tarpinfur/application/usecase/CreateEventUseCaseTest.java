package fr.yeensight.tarpinfur.application.usecase;

import fr.yeensight.tarpinfur.application.dto.CreateEventRequest;
import fr.yeensight.tarpinfur.application.dto.EventResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CreateEventUseCase.
 *
 * TESTING STRATEGY:
 * - No Spring context: tests run in milliseconds
 * - Uses FakeEventRepository (in-memory) for fast isolation
 * - Follows Given/When/Then pattern
 * - Tests both happy path and error scenarios
 * - Clear, intent-revealing test names
 *
 * CLEAN CODE: Readable test names explain intent without needing comments
 */
@DisplayName("CreateEventUseCase - Create new event")
class CreateEventUseCaseTest {

    private CreateEventUseCase useCase;
    private FakeEventRepository fakeRepository;

    @BeforeEach
    void setUp() {
        // Given: a clean repository and use case
        fakeRepository = new FakeEventRepository();
        useCase = new CreateEventUseCase(fakeRepository);
    }

    @Test
    @DisplayName("should create event successfully when request is valid")
    void should_create_event_when_valid_request() {
        // Given
        Instant futureDate = Instant.now().plus(7, ChronoUnit.DAYS);
        CreateEventRequest request = new CreateEventRequest(
            "Team Meeting",
            futureDate,
            Optional.of("Conference Room A")
        );

        // When
        EventResponse result = useCase.execute(request);

        // Then
        assertNotNull(result);
        assertEquals("Team Meeting", result.title());
        assertEquals(futureDate, result.date());
        assertEquals("Conference Room A", result.location().orElse(null));
        assertNotNull(result.id());
        assertFalse(result.id().isEmpty());

        // Verify it was persisted
        assertEquals(1, fakeRepository.size());
    }

    @Test
    @DisplayName("should create event without location when optional is empty")
    void should_create_event_without_location() {
        // Given
        Instant futureDate = Instant.now().plus(1, ChronoUnit.HOURS);
        CreateEventRequest request = new CreateEventRequest(
            "Quick Review",
            futureDate,
            Optional.empty()
        );

        // When
        EventResponse result = useCase.execute(request);

        // Then
        assertNotNull(result);
        assertEquals("Quick Review", result.title());
        assertTrue(result.location().isEmpty());
    }

    @Test
    @DisplayName("should fail when request is null")
    void should_fail_when_request_null() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(null));
    }

    @Test
    @DisplayName("should fail when event title is null")
    void should_fail_when_title_null() {
        // Given
        Instant futureDate = Instant.now().plus(7, ChronoUnit.DAYS);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new CreateEventRequest(null, futureDate, Optional.empty())
        );
    }

    @Test
    @DisplayName("should fail when event title is empty")
    void should_fail_when_title_empty() {
        // Given
        Instant futureDate = Instant.now().plus(7, ChronoUnit.DAYS);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new CreateEventRequest("   ", futureDate, Optional.empty())
        );
    }

    @Test
    @DisplayName("should fail when event date is null")
    void should_fail_when_date_null() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            new CreateEventRequest("Meeting", null, Optional.empty())
        );
    }

    @Test
    @DisplayName("should fail when event date is in the past")
    void should_fail_when_date_in_past() {
        // Given
        Instant pastDate = Instant.now().minus(1, ChronoUnit.DAYS);
        CreateEventRequest request = new CreateEventRequest(
            "Old Event",
            pastDate,
            Optional.empty()
        );

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(request));
    }

    @Test
    @DisplayName("should assign unique ID to each created event")
    void should_assign_unique_ids() {
        // Given
        Instant futureDate = Instant.now().plus(7, ChronoUnit.DAYS);
        CreateEventRequest request1 = new CreateEventRequest("Event 1", futureDate, Optional.empty());
        CreateEventRequest request2 = new CreateEventRequest("Event 2", futureDate, Optional.empty());

        // When
        EventResponse result1 = useCase.execute(request1);
        EventResponse result2 = useCase.execute(request2);

        // Then
        assertNotEquals(result1.id(), result2.id());
        assertEquals(2, fakeRepository.size());
    }
}

