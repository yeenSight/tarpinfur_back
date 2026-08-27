package fr.yeensight.tarpinfur.domain;

/**
 * Thrown when an Event is not found in the repository.
 * Represents a business rule violation, not a technical error.
 */
public class EventNotFoundException extends DomainException {
    public EventNotFoundException(EventId eventId) {
        super("Event not found: " + eventId);
    }
}

