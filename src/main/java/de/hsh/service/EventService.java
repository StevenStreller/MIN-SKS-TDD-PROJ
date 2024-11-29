package de.hsh.service;

import de.hsh.dto.Event;

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
}
