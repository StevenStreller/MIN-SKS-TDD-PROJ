package de.hsh.service;

import de.hsh.dto.Customer;
import de.hsh.dto.Event;
import de.hsh.dto.Reservation;

import java.util.ArrayList;
import java.util.List;

public class ReservationService {

    private final List<Reservation> reservations = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public Reservation getReservation(Event event, Customer customer) {
        for (Reservation reservation : reservations) {
            if (reservation.event() == event && reservation.customer() == customer) {
                return reservation;
            }
        }
        return null;
    }
}
