package de.hsh.service;

import de.hsh.dto.Event;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EventService {

    private final List<Event> events = new ArrayList<>();

    public List<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void serializeEvents(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(events);
        } catch (IOException e) {
            throw new RuntimeException("Serialisierung fehlgeschlagen");
        }
    }

    @SuppressWarnings("unchecked")
    public void deserializeEvents(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            List<Event> deserializedEvents = (List<Event>) in.readObject();
            events.clear();
            events.addAll(deserializedEvents);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Deserialisierung fehlgeschlagen");
        }
    }
}
