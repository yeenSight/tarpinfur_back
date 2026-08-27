package fr.yeensight.tarpinfur.infrastructure.persistence;

import fr.yeensight.tarpinfur.domain.Event;
import fr.yeensight.tarpinfur.domain.EventId;
import fr.yeensight.tarpinfur.domain.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for EventRepositoryAdapter with MongoDB.
 *
 * - @DataMongoTest loads only the MongoDB slice of the Spring context.
 * - Testcontainers starts a real MongoDB instance for deterministic tests.
 * - The adapter is imported explicitly so the domain port is exercised through Spring.
 */
@DataMongoTest
@Testcontainers
@Import(EventRepositoryAdapter.class)
@DisplayName("EventRepositoryAdapter - MongoDB integration")
class EventRepositoryAdapterTest {

    @Container
    static final MongoDBContainer mongoDbContainer =
        new MongoDBContainer(DockerImageName.parse("mongo:7"));

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongoDbContainer.getReplicaSetUrl("tarpinfur_test"));
    }

    @Autowired
    private EventSpringDataRepository springDataRepository;

    @Autowired
    private EventRepository eventRepository;

    private Instant now;
    private Instant tomorrow;

    @BeforeEach
    void setUp() {
        springDataRepository.deleteAll();
        now = Instant.now();
        tomorrow = now.plus(1, ChronoUnit.DAYS);
    }

    @Test
    @DisplayName("should save and retrieve event from MongoDB")
    void should_save_and_retrieve_event() {
        // Given: a domain event
        EventId eventId = new EventId("test-1");
        Event event = Event.create(eventId, "Integration Test Meeting", tomorrow, Optional.of("Room A"));

        // When: save event
        Event saved = eventRepository.save(event);

        // Then: verify it was saved
        assertNotNull(saved);
        assertEquals("test-1", saved.id().value());
        assertEquals("Integration Test Meeting", saved.title());
        assertEquals(tomorrow, saved.date());
        assertEquals("Room A", saved.location().orElse(null));

        // And: verify we can retrieve it
        Optional<Event> retrieved = eventRepository.findById(eventId);
        assertTrue(retrieved.isPresent());
        assertEquals(event.title(), retrieved.get().title());
    }

    @Test
    @DisplayName("should return empty Optional when event not found")
    void should_return_empty_when_not_found() {
        // When
        Optional<Event> result = eventRepository.findById(new EventId("nonexistent"));

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("should find events by date range")
    void should_find_events_by_date() {
        // Given: multiple events at different dates
        Event event1 = Event.create(new EventId("1"), "Event 1", tomorrow, Optional.empty());
        Event event2 = Event.create(new EventId("2"), "Event 2", tomorrow.plus(7, ChronoUnit.DAYS), Optional.empty());
        Event event3 = Event.create(new EventId("3"), "Event 3", tomorrow.plus(14, ChronoUnit.DAYS), Optional.empty());

        eventRepository.save(event1);
        eventRepository.save(event2);
        eventRepository.save(event3);

        // When: find events from tomorrow onwards
        List<Event> results = eventRepository.findByDateGreaterThanOrEqual(tomorrow);

        // Then: all 3 events should be found
        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch(e -> !e.date().isBefore(tomorrow)));
    }

    @Test
    @DisplayName("should delete event by ID")
    void should_delete_event() {
        // Given: an event in database
        EventId eventId = new EventId("delete-test");
        Event event = Event.create(eventId, "To Delete", tomorrow, Optional.empty());
        eventRepository.save(event);

        // Verify it's saved
        assertTrue(eventRepository.findById(eventId).isPresent());

        // When: delete it
        eventRepository.deleteById(eventId);

        // Then: it should be gone
        assertFalse(eventRepository.findById(eventId).isPresent());
    }

    @Test
    @DisplayName("should find all events")
    void should_find_all_events() {
        // Given: multiple events
        eventRepository.save(Event.create(new EventId("1"), "Event 1", tomorrow, Optional.empty()));
        eventRepository.save(Event.create(new EventId("2"), "Event 2", tomorrow.plus(1, ChronoUnit.DAYS), Optional.empty()));

        // When
        List<Event> allEvents = eventRepository.findAll();

        // Then
        assertEquals(2, allEvents.size());
    }

    @Test
    @DisplayName("should preserve location when saving and retrieving")
    void should_preserve_optional_location() {
        // Given: event with and without location
        Event withLocation = Event.create(new EventId("with"), "With Loc", tomorrow, Optional.of("Building X"));
        Event withoutLocation = Event.create(new EventId("without"), "Without Loc", tomorrow, Optional.empty());

        // When
        eventRepository.save(withLocation);
        eventRepository.save(withoutLocation);

        // Then
        Optional<Event> retrieved1 = eventRepository.findById(new EventId("with"));
        Optional<Event> retrieved2 = eventRepository.findById(new EventId("without"));

        assertTrue(retrieved1.isPresent());
        assertEquals("Building X", retrieved1.get().location().orElse(null));

        assertTrue(retrieved2.isPresent());
        assertTrue(retrieved2.get().location().isEmpty());
    }
}

