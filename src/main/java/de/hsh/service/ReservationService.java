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
    private final EmailService emailService;

    public ReservationService(BlacklistService blacklistService, EmailService emailService) {
        this.blacklistService = blacklistService;
        this.emailService = emailService;
    }

    public void addReservation(Reservation reservation) {
        checkIfCustomerIsBlacklisted(reservation.customer());
        checkIfReservedAreGreaterThanAvailableSeats(reservation);
        checkIfCustomerShouldGetAnEmail(reservation);

        mergeReservation(reservation);
    }

    private void mergeReservation(Reservation reservation) {
        // Prüfen, ob bereits eine Buchung für den Kunden und das Event existiert
        for (Reservation existingReservation : reservations) {
            // Prüfen, ob der Kunde und das Event übereinstimmen
            if (existingReservation.customer().name().equals(reservation.customer().name()) &&
                    existingReservation.event().title().equals(reservation.event().title())) {

                // Wenn eine bestehende Buchung gefunden wird, erstellen wir einen neuen Reservation-Record
                // mit der ID der neuen Buchung (neuere Buchung übernimmt die ID)
                Reservation mergedReservation = new Reservation(
                        reservation.uuid(), // Die ID der neueren Buchung wird übernommen
                        reservation.event(), // Das Event bleibt gleich
                        reservation.customer(), // Der Kunde bleibt gleich
                        existingReservation.reservedSeats() + reservation.reservedSeats() // Die reservierten Plätze werden zusammengeführt
                );

                // Entferne die alte Buchung und füge die neue zusammengeführte hinzu
                reservations.remove(existingReservation);
                reservations.add(mergedReservation);

                // Die Methode beendet die Ausführung, wenn die Buchungen zusammengeführt wurden
                return;
            }
        }

        // Falls keine bestehende Buchung gefunden wurde, fügen wir die neue Buchung hinzu
        reservations.add(reservation);
    }





    private void checkIfCustomerShouldGetAnEmail(Reservation reservation) {
        if (reservation.reservedSeats() >= (reservation.event().totalSeats() * 0.1)) {
            String email = reservation.event().organizerEmail();
            String subject = "Buchung für " + reservation.event().title() + " bestätigt";
            String message = "Es wurden " + reservation.reservedSeats() + " Plätze für die Veranstaltung " + reservation.event().title() + " reserviert.";
            emailService.sendEmail(email, subject, message);
        }
    }

    private void checkIfCustomerIsBlacklisted(Customer customer) {
        if (blacklistService.isBlacklisted(customer.name())) {
            throw new IllegalArgumentException("Der Kunde befindet sich auf einer Blacklist und kann deshalb keine Buchung durchführen.");
        }
    }

    private void checkIfReservedAreGreaterThanAvailableSeats(Reservation reservation) {
        int totalReservedSeats = reservations.stream()
                .filter(r -> r.event().equals(reservation.event()))
                .mapToInt(Reservation::reservedSeats)
                .sum();

        totalReservedSeats += reservation.reservedSeats();

        if (totalReservedSeats > reservation.event().totalSeats()) {
            throw new IllegalArgumentException("Die Gesamtzahl der reservierten Plätze überschreitet die verfügbaren Plätze.");
        }
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
