package fr.yeensight.tarpinfur.application.usecase;

import fr.yeensight.tarpinfur.domain.Event;
import fr.yeensight.tarpinfur.domain.EventId;
import fr.yeensight.tarpinfur.domain.EventRepository;

import java.time.Instant;
import java.util.*;

/**
 * Fake EventRepository for testing purposes.
 * Stores events in memory (no persistence).
 *
 * TESTING PRINCIPLE: Use fakes instead of mocks when possible.
 * - Fakes are simpler, more readable, and don't require Mockito
 * - Ideal for unit tests to stay fast and focused
 * - This is NOT production code; it's test infrastructure
 */
public class FakeEventRepository implements EventRepository {
    private final Map<String, Event> events = new HashMap<>();

    @Override
    public Event save(Event event) {
        events.put(event.id().value(), event);
        return event;
    }

    @Override
    public Optional<Event> findById(EventId id) {
        return Optional.ofNullable(events.get(id.value()));
    }

    @Override
    public List<Event> findByDateGreaterThanOrEqual(Instant fromDate) {
        return events.values().stream()
            .filter(e -> !e.date().isBefore(fromDate))
            .sorted(Comparator.comparing(Event::date))
            .toList();
    }

    @Override
    public List<Event> findAll() {
        return new ArrayList<>(events.values());
    }

    @Override
    public void deleteById(EventId id) {
        events.remove(id.value());
    }

    /**
     * Test helper: check how many events are stored.
     */
    public int size() {
        return events.size();
    }

    /**
     * Test helper: clear all events.
     */
    public void clear() {
        events.clear();
    }
}

