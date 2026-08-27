package fr.yeensight.tarpinfur.infrastructure.config;

import fr.yeensight.tarpinfur.application.usecase.CreateEventUseCase;
import fr.yeensight.tarpinfur.application.usecase.GetEventsUseCase;
import fr.yeensight.tarpinfur.domain.EventRepository;
import fr.yeensight.tarpinfur.infrastructure.persistence.EventRepositoryAdapter;
import fr.yeensight.tarpinfur.infrastructure.persistence.EventSpringDataRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for dependency injection.
 *
 * KEY PRINCIPLE - Composition Root:
 * - All beans (Use Cases, Adapters) are wired here
 * - Application layer doesn't need to know about Spring
 * - Infrastructure adapters are registered as implementations of domain ports
 * - Makes testing easy: can create use cases with fake repositories without Spring
 *
 * BENEFITS:
 * - Centralized DI configuration
 * - Clear understanding of dependencies
 * - Easy to swap implementations (e.g., for testing)
 * - Application layer remains Spring-free
 */
@Configuration
public class EventConfiguration {

    /**
     * Provide the EventRepository port implementation.
     * Spring will inject EventRepositoryAdapter as EventRepository interface.
     */
    @Bean
    public EventRepository eventRepository(EventSpringDataRepository springDataRepository) {
        return new EventRepositoryAdapter(springDataRepository);
    }

    /**
     * Provide the CreateEventUseCase.
     * Automatically injected with EventRepository (interface).
     */
    @Bean
    public CreateEventUseCase createEventUseCase(EventRepository eventRepository) {
        return new CreateEventUseCase(eventRepository);
    }

    /**
     * Provide the GetEventsUseCase.
     * Automatically injected with EventRepository (interface).
     */
    @Bean
    public GetEventsUseCase getEventsUseCase(EventRepository eventRepository) {
        return new GetEventsUseCase(eventRepository);
    }
}

