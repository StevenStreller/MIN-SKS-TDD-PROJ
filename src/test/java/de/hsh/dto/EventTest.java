package de.hsh.dto;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    public void totalSeats() {
        int expectedSeats = 50;
        Event event = new Event(UUID.randomUUID(), "Test Event", new Date(), 100.0, expectedSeats);

        assertEquals(expectedSeats, event.totalSeats(), "Die verfügbaren Plätze sollten korrekt zurückgegeben werden.");
    }

    @Test
    public void totalSeatsWithDifferentValues() {
        Event event1 = new Event(UUID.randomUUID(), "Event 1", new Date(), 100.0, 30);
        Event event2 = new Event(UUID.randomUUID(), "Event 2", new Date(), 120.0, 75);

        assertEquals(30, event1.totalSeats(), "Das erste Event sollte 30 verfügbare Plätze haben.");
        assertEquals(75, event2.totalSeats(), "Das zweite Event sollte 75 verfügbare Plätze haben.");
    }

    @Test
    public void totalSeatsZero() {
        Event event = new Event(UUID.randomUUID(), "Event No Seats", new Date(), 50.0, 0);

        assertEquals(0, event.totalSeats(), "Das Event sollte 0 verfügbare Plätze haben.");
    }


    @Test
    public void totalSeatsCannotBeNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Event(UUID.randomUUID(), "Test Event", new Date(), 100.0, -5));
        assertEquals("Die verfügbaren Plätze dürfen nicht negativ sein.", exception.getMessage());
    }

    @Test
    public void priceCannotBeNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Event(UUID.randomUUID(), "Test Event", new Date(), -10.0, 50));
        assertEquals("Der Preis darf nicht negativ sein.", exception.getMessage());
    }

}