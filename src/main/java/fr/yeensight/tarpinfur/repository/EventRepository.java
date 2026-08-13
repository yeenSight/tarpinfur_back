package fr.yeensight.tarpinfur.repository;

import fr.yeensight.tarpinfur.model.EventEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface EventRepository extends MongoRepository<EventEntity, String>, PagingAndSortingRepository<EventEntity, String> {
    Iterable<EventEntity> findByDateGreaterThanEqual(Date now);
}
