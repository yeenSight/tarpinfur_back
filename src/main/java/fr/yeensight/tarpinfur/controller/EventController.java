package fr.yeensight.tarpinfur.controller;

import fr.yeensight.tarpinfur.model.EventEntity;
import fr.yeensight.tarpinfur.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Create a new event
     * @param eventEntity
     * @return
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventEntity createEvent(@RequestBody EventEntity eventEntity) {
        return eventService.createEvent(eventEntity);
    }

    /**
     * retrieve all events for a given month and year
     * @return
     */
    @GetMapping
    public Iterable<EventEntity> getEventsByMonthAndYear() {
        return eventService.getAllEvent();
    }

    /**
     * create multiple events at once
     * @param eventEntities
     * @return
     */
    @PostMapping("/addEvents")
    @ResponseStatus(HttpStatus.CREATED)
    public  List<EventEntity> createEvents(@RequestBody List<EventEntity> eventEntities) {
        List<EventEntity> createdEvents = new ArrayList<>();
        for (EventEntity eventEntity : eventEntities) {
            createdEvents.add(eventService.createEvent(eventEntity));
        }
        return createdEvents;
    }
}
