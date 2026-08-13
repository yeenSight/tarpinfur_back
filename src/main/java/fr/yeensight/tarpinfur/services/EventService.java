package fr.yeensight.tarpinfur.services;

import fr.yeensight.tarpinfur.model.EventEntity;
import fr.yeensight.tarpinfur.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public EventEntity createEvent(EventEntity eventEntity) {
        return eventRepository.save(eventEntity);
    }

    public Iterable<EventEntity> getAllEvent() {
        Date now = new Date();
        return eventRepository.findByDateGreaterThanEqual(now);
    }
}
