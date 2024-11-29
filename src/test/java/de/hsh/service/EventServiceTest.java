package de.hsh.service;

import de.hsh.dto.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventService();
    }

    @Test
    public void eventsListIsInitiallyEmpty() {
        assertTrue(eventService.getEvents().isEmpty(), "Die Event-Liste sollte zu Beginn leer sein.");
    }

    @Test
    public void addEventSuccessfully() {
        Event event = new Event(UUID.randomUUID(), "Test Event", new Date(), 100.0, 50);
        eventService.addEvent(event);

        assertFalse(eventService.getEvents().isEmpty(), "Die Event-Liste sollte nach dem Hinzufügen eines Events nicht leer sein.");
        assertTrue(eventService.getEvents().contains(event), "Das hinzugefügte Event sollte in der Liste enthalten sein.");
    }

    @Test
    public void addEventWhenListContainsNoMatchingEvent() {
        Event event1 = new Event(UUID.randomUUID(), "Test Event 1", new Date(), 100.0, 50);
        Event event2 = new Event(UUID.randomUUID(), "Test Event 2", new Date(), 150.0, 30);
        eventService.addEvent(event1);

        assertFalse(eventService.getEvents().contains(event2), "Die Event-Liste sollte das Event nicht enthalten, das nicht hinzugefügt wurde.");
    }

    @Test
    public void addMultipleEvents() {
        Event event1 = new Event(UUID.randomUUID(), "Test Event 1", new Date(), 100.0, 50);
        Event event2 = new Event(UUID.randomUUID(), "Test Event 2", new Date(), 150.0, 30);
        eventService.addEvent(event1);
        eventService.addEvent(event2);

        assertEquals(2, eventService.getEvents().size(), "Die Event-Liste sollte nach dem Hinzufügen von zwei Events genau zwei Events enthalten.");
        assertTrue(eventService.getEvents().contains(event1), "Die Event-Liste sollte das erste Event enthalten.");
        assertTrue(eventService.getEvents().contains(event2), "Die Event-Liste sollte das zweite Event enthalten.");
    }
}