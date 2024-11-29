package de.hsh.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public record Event(UUID identifier, String title, Date date, double price, int totalSeats, String organizerEmail) implements Serializable {

    public Event {
        if (price < 0) {
            throw new IllegalArgumentException("Der Preis darf nicht negativ sein.");
        }

        if (totalSeats < 0) {
            throw new IllegalArgumentException("Die verfügbaren Plätze dürfen nicht negativ sein.");
        }
    }


}
