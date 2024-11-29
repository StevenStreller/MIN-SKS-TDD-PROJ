package de.hsh.service;

import de.hsh.dto.Customer;
import de.hsh.dto.Event;
import de.hsh.dto.Reservation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {

    private final List<Reservation> reservations = new ArrayList<>();
    private final BlacklistService blacklistService;
    public ReservationService(BlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

    public void addReservation(Reservation reservation) {
        if (blacklistService.isBlacklisted(reservation.customer().name())) {
            throw new IllegalArgumentException("Der Kunde befindet sich auf einer Blacklist und kann deshalb keine Buchung durchführen.");
        }

        int totalReservedSeats = reservations.stream()
                .filter(r -> r.event().equals(reservation.event()))
                .mapToInt(Reservation::reservedSeats)
                .sum();

        totalReservedSeats += reservation.reservedSeats();

        if (totalReservedSeats > reservation.event().totalSeats()) {
            throw new IllegalArgumentException("Die Gesamtzahl der reservierten Plätze überschreitet die verfügbaren Plätze.");
        }

        reservations.add(reservation);
    }

    public int getAvailableSeats(Event event) {
        // Berechne die Summe der reservierten Plätze für das Event
        int reservedSeats = reservations.stream()
                .filter(reservation -> reservation.event().equals(event))
                .mapToInt(Reservation::reservedSeats)
                .sum();

        return event.totalSeats() - reservedSeats;
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
