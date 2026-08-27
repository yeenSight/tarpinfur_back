package fr.yeensight.tarpinfur.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data MongoDB repository for EventEntity.
 *
 * This interface is SPECIFIC to the infrastructure layer.
 * It's NOT the domain port (which is fr.yeensight.tarpinfur.domain.EventRepository).
 *
 * SEPARATION:
 * - Domain layer: defines EventRepository interface (port) - FRAMEWORK INDEPENDENT
 * - Infrastructure: implements that port with EventRepositoryAdapter
 * - Infrastructure: uses this Spring Data interface internally
 *
 * The domain doesn't know about MongoRepository, Spring Data, or this class.
 */
@Repository
public interface EventSpringDataRepository extends MongoRepository<EventEntity, String> {
    /**
     * Find all events with date >= fromDate, sorted ascending.
     */
    List<EventEntity> findByDateGreaterThanOrEqualOrderByDateAsc(Instant fromDate);
}

