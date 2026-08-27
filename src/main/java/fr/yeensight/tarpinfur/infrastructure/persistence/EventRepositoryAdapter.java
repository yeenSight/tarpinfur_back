package fr.yeensight.tarpinfur.infrastructure.persistence;

import fr.yeensight.tarpinfur.domain.Event;
import fr.yeensight.tarpinfur.domain.EventId;
import fr.yeensight.tarpinfur.domain.EventRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Adapter: implements the Domain EventRepository port using Spring Data MongoDB.
 *
 * KEY PRINCIPLE - Hexagonal Architecture (Ports & Adapters):
 * - Implements domain.EventRepository interface (the PORT)
 * - Uses EventSpringDataRepository internally (concrete tech)
 * - Translates between domain Event ↔ JPA EventEntity
 * - Domain never knows about Spring Data, MongoDB, or this adapter
 *
 * DEPENDENCY INVERSION (SOLID - DIP):
 * - Domain → depends on EventRepository (interface)
 * - Application → depends on EventRepository (interface)
 * - Infrastructure → implements EventRepository (interface)
 * - Spring wires this implementation to domain/app via DI
 *
 * BENEFITS:
 * - Domain layer is testable WITHOUT Spring
 * - Can swap MongoDB for PostgreSQL without touching domain
 * - Explicit mapping between technical and business concerns
 */
@Repository
public class EventRepositoryAdapter implements EventRepository {

    private final EventSpringDataRepository springDataRepository;

    public EventRepositoryAdapter(EventSpringDataRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Event save(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event must not be null");
        }

        // Map domain → JPA
        EventEntity entity = EventMapper.toEntity(event);

        // Persist
        EventEntity savedEntity = springDataRepository.save(entity);

        // Map JPA → domain
        return EventMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Event> findById(EventId id) {
        if (id == null) {
            throw new IllegalArgumentException("EventId must not be null");
        }

        return springDataRepository.findById(id.value())
            .map(EventMapper::toDomain);
    }

    @Override
    public List<Event> findByDateGreaterThanOrEqual(Instant fromDate) {
        if (fromDate == null) {
            throw new IllegalArgumentException("fromDate must not be null");
        }

        return springDataRepository.findByDateGreaterThanOrEqualOrderByDateAsc(fromDate)
            .stream()
            .map(EventMapper::toDomain)
            .toList();
    }

    @Override
    public List<Event> findAll() {
        return springDataRepository.findAll()
            .stream()
            .map(EventMapper::toDomain)
            .toList();
    }

    @Override
    public void deleteById(EventId id) {
        if (id == null) {
            throw new IllegalArgumentException("EventId must not be null");
        }

        springDataRepository.deleteById(id.value());
    }
}

