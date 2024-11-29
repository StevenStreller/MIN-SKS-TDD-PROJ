package de.hsh.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public record Event(UUID identifier, String title, Date date, double price, int availableSeats) implements Serializable {

    public Event {
        if (price < 0) {
            throw new IllegalArgumentException("Der Preis darf nicht negativ sein.");
        }

        if (availableSeats < 0) {
            throw new IllegalArgumentException("Die verfügbaren Plätze dürfen nicht negativ sein.");
        }
    }

    @Override
    public int availableSeats() {
        return availableSeats;
    }
}
