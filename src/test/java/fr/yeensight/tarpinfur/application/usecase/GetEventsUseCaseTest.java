package fr.yeensight.tarpinfur.application.usecase;

import fr.yeensight.tarpinfur.application.dto.EventResponse;
import fr.yeensight.tarpinfur.domain.Event;
import fr.yeensight.tarpinfur.domain.EventId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GetEventsUseCase.
 *
 * TESTING STRATEGY:
 * - No Spring context: tests run in milliseconds
 * - Uses FakeEventRepository for isolation
 * - Tests filtering by date, empty results, sorting
 * - Clear test names explaining intent
 */
@DisplayName("GetEventsUseCase - Retrieve events")
class GetEventsUseCaseTest {

    private GetEventsUseCase useCase;
    private FakeEventRepository fakeRepository;
    private Instant now;
    private Instant tomorrow;
    private Instant nextWeek;
    private Instant yesterday;

    @BeforeEach
    void setUp() {
        // Given: a clean repository and use case
        fakeRepository = new FakeEventRepository();
        useCase = new GetEventsUseCase(fakeRepository);

        // Setup test dates
        now = Instant.now();
        tomorrow = now.plus(1, ChronoUnit.DAYS);
        nextWeek = now.plus(7, ChronoUnit.DAYS);
        yesterday = now.minus(1, ChronoUnit.DAYS);

        // Populate test data
        setupTestData();
    }

    private void setupTestData() {
        // Add events at various dates
        Event event1 = Event.create(new EventId("1"), "Meeting Today", tomorrow, Optional.of("Room A"));
        Event event2 = Event.create(new EventId("2"), "Standup Next Week", nextWeek, Optional.empty());
        Event event3 = Event.create(new EventId("3"), "Conference", nextWeek.plus(1, ChronoUnit.HOURS), Optional.of("Hall B"));

        fakeRepository.save(event1);
        fakeRepository.save(event2);
        fakeRepository.save(event3);
    }

    @Test
    @DisplayName("should retrieve all events from current date onwards")
    void should_retrieve_events_from_now() {
        // When
        List<EventResponse> results = useCase.execute(now);

        // Then
        assertEquals(3, results.size());
        assertEquals("Meeting Today", results.get(0).title());
    }

    @Test
    @DisplayName("should retrieve only future events after a given date")
    void should_retrieve_only_events_after_given_date() {
        // When
        List<EventResponse> results = useCase.execute(nextWeek);

        // Then
        assertEquals(2, results.size()); // Only events >= nextWeek
        assertTrue(results.stream().allMatch(e -> e.date().isAfter(nextWeek) || e.date().equals(nextWeek)));
    }

    @Test
    @DisplayName("should return empty list when no events exist after date")
    void should_return_empty_when_no_events_after_date() {
        // Given: a date far in the future
        Instant farFuture = nextWeek.plus(100, ChronoUnit.DAYS);

        // When
        List<EventResponse> results = useCase.execute(farFuture);

        // Then
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("should fail when fromDate is null")
    void should_fail_when_date_null() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(null));
    }

    @Test
    @DisplayName("should retrieve all events when executeGetAll is called")
    void should_retrieve_all_events() {
        // When
        List<EventResponse> results = useCase.executeGetAll();

        // Then
        assertEquals(3, results.size());
    }

    @Test
    @DisplayName("should return empty list for getAllEvents when repository is empty")
    void should_return_empty_when_no_events() {
        // Given: empty repository
        fakeRepository.clear();

        // When
        List<EventResponse> results = useCase.executeGetAll();

        // Then
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("should preserve location information when retrieving events")
    void should_preserve_location() {
        // When
        List<EventResponse> results = useCase.executeGetAll();

        // Then
        EventResponse withLocation = results.stream()
            .filter(e -> e.location().isPresent())
            .findFirst()
            .orElse(null);

        assertNotNull(withLocation);
        assertEquals("Room A", withLocation.location().get());
    }

    @Test
    @DisplayName("should return events sorted by date ascending")
    void should_return_sorted_by_date() {
        // When
        List<EventResponse> results = useCase.execute(now);

        // Then
        for (int i = 0; i < results.size() - 1; i++) {
            assertFalse(results.get(i).date().isAfter(results.get(i + 1).date()),
                "Events should be sorted by date ascending");
        }
    }
}

