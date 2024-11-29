package de.hsh.service;

import de.hsh.dto.Customer;
import de.hsh.dto.Event;
import de.hsh.dto.Reservation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {

    private final List<Reservation> reservations = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public Reservation getReservation(Event event, Customer customer) {
        for (Reservation reservation : reservations) {
            if (reservation.event() == event && reservation.customer() == customer) {
                return reservation;
            }
        }
        return null;
    }

    public void serializeReservations(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(reservations);
        } catch (IOException e) {
            throw new RuntimeException("Serialisierung fehlgeschlagen");
        }
    }

    @SuppressWarnings("unchecked")
    public void deserializeReservations(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            List<Reservation> deserializedReservations = (List<Reservation>) in.readObject();
            reservations.clear();
            reservations.addAll(deserializedReservations);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Deserialisierung fehlgeschlagen");
        }
    }
}
