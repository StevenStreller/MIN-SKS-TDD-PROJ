package de.hsh.dto;

import java.util.UUID;

public record Reservation(UUID uuid, Event event, Customer customer, int reservedSeats) {
    public Reservation {
        if (uuid == null) throw new IllegalArgumentException("UUID darf nicht null sein");

        if (event == null) throw new IllegalArgumentException("Event darf nicht null sein");

        if (customer == null) throw new IllegalArgumentException("Kunde darf nicht null sein");

        if (reservedSeats <= 0) throw new IllegalArgumentException("Reservierte Plätze müssen größer als null sein");

        if (reservedSeats > event.availableSeats())
            throw new IllegalArgumentException("Reservierte Plätze dürfen nicht größer als verfügbare Sitzplatzanzahl sein.");
    }
}
