package fr.yeensight.tarpinfur.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.yeensight.tarpinfur.application.dto.CreateEventRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for EventController with full Spring Boot context.
 *
 * TESTING STRATEGY:
 * - @SpringBootTest: full application context with MongoDB
 * - MockMvc: tests HTTP endpoints without starting network server
 * - Tests the full request → controller → usecase → adapter → db flow
 *
 * WHAT WE'RE TESTING:
 * - HTTP status codes (201, 200, 400)
 * - Request/response DTOs serialization
 * - End-to-end flow from REST API to database and back
 * - Error handling
 *
 * NOTE: These tests are slower than unit tests because they start the full app.
 * Use sparingly for critical paths. Most tests should be unit tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("EventController - HTTP integration tests")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("should create event and return 201")
    void should_create_event() throws Exception {
        // Given
        Instant futureDate = Instant.now().plus(7, ChronoUnit.DAYS);
        CreateEventRequest request = new CreateEventRequest(
            "Team Planning",
            futureDate,
            Optional.of("Conference Hall")
        );

        // When & Then
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.title").value("Team Planning"))
            .andExpect(jsonPath("$.location").value("Conference Hall"));
    }

    @Test
    @DisplayName("should get all events")
    void should_get_all_events() throws Exception {
        // Given: create an event first
        Instant futureDate = Instant.now().plus(1, ChronoUnit.DAYS);
        CreateEventRequest request = new CreateEventRequest(
            "Standup",
            futureDate,
            Optional.empty()
        );

        mockMvc.perform(post("/api/events")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        // When & Then: get all events
        mockMvc.perform(get("/api/events"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].title").value("Standup"));
    }

    @Test
    @DisplayName("should return 400 when creating event with invalid data")
    void should_fail_with_bad_request() throws Exception {
        // Given: invalid request (empty title)
        CreateEventRequest request = new CreateEventRequest(
            "   ",
            Instant.now().plus(7, ChronoUnit.DAYS),
            Optional.empty()
        );

        // When & Then
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should create multiple events via batch endpoint")
    void should_create_batch_events() throws Exception {
        // Given
        Instant date1 = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant date2 = Instant.now().plus(2, ChronoUnit.DAYS);

        CreateEventRequest req1 = new CreateEventRequest("Event 1", date1, Optional.empty());
        CreateEventRequest req2 = new CreateEventRequest("Event 2", date2, Optional.of("Room B"));

        String requestBody = objectMapper.writeValueAsString(new CreateEventRequest[]{req1, req2});

        // When & Then
        mockMvc.perform(post("/api/events/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value("Event 1"))
            .andExpect(jsonPath("$[1].title").value("Event 2"));
    }
}

