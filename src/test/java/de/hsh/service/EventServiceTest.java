package de.hsh.service;

import de.hsh.dto.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    private EventService eventService;
    private Event event1;
    private Event event2;

    @BeforeEach
    void setUp() {
        eventService = new EventService();
        event1 = new Event(UUID.randomUUID(), "Konzert 1", new Date(), 50.0, 100);
        event2 = new Event(UUID.randomUUID(), "Konzert 2", new Date(), 60.0, 150);
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
    void serializationAndDeserialization() {
        // Add events to the list
        eventService.addEvent(event1);
        eventService.addEvent(event2);

        // Serialize the event list
        String filename = "events.ser";
        eventService.serializeEvents(filename);

        // Create a new EventService and deserialize the list
        EventService newEventService = new EventService();
        newEventService.deserializeEvents(filename);

        // Check that the deserialized list contains the same events
        List<Event> deserializedEvents = newEventService.getEvents();
        assertEquals(2, deserializedEvents.size(), "Deserialized event list should have the same size as the original list");
        assertTrue(deserializedEvents.contains(event1), "Event 1 should be present in the deserialized list");
        assertTrue(deserializedEvents.contains(event2), "Event 2 should be present in the deserialized list");
    }

    @Test
    void serializationWithEmptyList() {
        // Serialize an empty list
        String filename = "empty_events.ser";
        eventService.serializeEvents(filename);

        // Deserialize into a new service
        EventService newEventService = new EventService();
        newEventService.deserializeEvents(filename);

        // Assert the list is still empty after deserialization
        assertTrue(newEventService.getEvents().isEmpty(), "Deserialized event list should be empty for an empty list");
    }

    @Test
    void serializationWithOneEvent() {
        // Add a single event and serialize the list
        eventService.addEvent(event1);
        String filename = "single_event.ser";
        eventService.serializeEvents(filename);

        // Create a new EventService and deserialize
        EventService newEventService = new EventService();
        newEventService.deserializeEvents(filename);

        // Assert the deserialized list contains one event
        List<Event> deserializedEvents = newEventService.getEvents();
        assertEquals(1, deserializedEvents.size(), "Deserialized event list should contain 1 event");
        assertTrue(deserializedEvents.contains(event1), "Deserialized list should contain the event");
    }

    @Test
    void deserializationFileNotFound() {
        String filename = "non_existent_file.ser";
        EventService newEventService = new EventService();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> newEventService.deserializeEvents(filename));
        assertEquals("Deserialisierung fehlgeschlagen", exception.getMessage(), "Deserialization should fail with the correct message when the file does not exist");
    }

    @Test
    void deserializationWithCorruptedFile() {
        String filename = "corrupted_events.ser";
        File file = new File(filename);

        // Create a corrupted file
        try {
            if (file.exists()) file.delete();
            file.createNewFile();
            // Write some invalid content to the file (not a valid serialized object)
            try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                writer.write("Invalid data");
            }

            EventService newEventService = new EventService();

            RuntimeException exception = assertThrows(RuntimeException.class, () -> newEventService.deserializeEvents(filename));
            assertEquals("Deserialisierung fehlgeschlagen", exception.getMessage(), "Deserialization should fail with the correct message when the file is corrupted");
        } catch (IOException e) {
            fail("Failed to create corrupted file: " + e.getMessage());
        }
    }

    @Test
    void serializationAndDeserializationWithDifferentEventData() {
        Event newEvent = new Event(UUID.randomUUID(), "Festival", new Date(), 80.0, 500);

        // Serialize the new event
        String filename = "new_event.ser";
        eventService.addEvent(newEvent);
        eventService.serializeEvents(filename);

        // Create a new EventService and deserialize
        EventService newEventService = new EventService();
        newEventService.deserializeEvents(filename);

        // Assert that the new event is in the deserialized list
        List<Event> deserializedEvents = newEventService.getEvents();
        assertEquals(1, deserializedEvents.size(), "Deserialized list should contain the new event");
        assertTrue(deserializedEvents.contains(newEvent), "Deserialized list should contain the new event");
    }

}