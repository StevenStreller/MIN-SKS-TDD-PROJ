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

    @Test
    public void availableSeats() {
        // Erstelle ein Event mit einer bestimmten Anzahl verfügbarer Plätze
        int expectedSeats = 50;
        Event event = new Event(UUID.randomUUID(), "Test Event", new Date(), 100.0, expectedSeats);

        // Teste, ob die Methode `availableSeats` den richtigen Wert zurückgibt
        assertEquals(expectedSeats, event.availableSeats(), "Die verfügbaren Plätze sollten korrekt zurückgegeben werden.");
    }

    @Test
    public void availableSeatsWithDifferentValues() {
        // Teste verschiedene Werte für die verfügbaren Plätze
        Event event1 = new Event(UUID.randomUUID(), "Event 1", new Date(), 100.0, 30);
        Event event2 = new Event(UUID.randomUUID(), "Event 2", new Date(), 120.0, 75);

        // Überprüfe, ob die richtigen Werte für `availableSeats` zurückgegeben werden
        assertEquals(30, event1.availableSeats(), "Das erste Event sollte 30 verfügbare Plätze haben.");
        assertEquals(75, event2.availableSeats(), "Das zweite Event sollte 75 verfügbare Plätze haben.");
    }

    @Test
    public void availableSeatsZero() {
        // Teste den Fall, dass keine Plätze verfügbar sind (0)
        Event event = new Event(UUID.randomUUID(), "Event No Seats", new Date(), 50.0, 0);

        // Überprüfe, ob `availableSeats` den Wert 0 zurückgibt
        assertEquals(0, event.availableSeats(), "Das Event sollte 0 verfügbare Plätze haben.");
    }



    /**
     * OPTIONALE TESTS
     */

    @Test
    public void availableSeatsCannotBeNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Event(UUID.randomUUID(), "Test Event", new Date(), 100.0, -5));
        assertEquals("Die verfügbaren Plätze dürfen nicht negativ sein.", exception.getMessage());
    }

    @Test
    public void priceCannotBeNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Event(UUID.randomUUID(), "Test Event", new Date(), -10.0, 50));
        assertEquals("Der Preis darf nicht negativ sein.", exception.getMessage());
    }
}