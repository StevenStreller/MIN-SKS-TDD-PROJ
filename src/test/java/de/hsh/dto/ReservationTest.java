package de.hsh.dto;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    @Test
    void validReservation() {
        Customer customer = new Customer("Max Mustermann", "Musterstraße 1");
        Event event = new Event(UUID.randomUUID(), "Konzert", new Date(), 50.0, 100);
        UUID reservationUUID = UUID.randomUUID();

        Reservation reservation = new Reservation(reservationUUID, event, customer, 10);

        assertNotNull(reservation);
        assertEquals(reservationUUID, reservation.uuid());
        assertEquals(event, reservation.event());
        assertEquals(customer, reservation.customer());
        assertEquals(10, reservation.reservedSeats());
    }

    @Test
    void reservationWithNullUUID() {
        Customer customer = new Customer("Max Mustermann", "Musterstraße 1");
        Event event = new Event(UUID.randomUUID(), "Konzert", new Date(), 50.0, 100);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Reservation(null, event, customer, 10)
        );
        assertEquals("UUID darf nicht null sein", exception.getMessage());
    }

    @Test
    void reservationWithNullEvent() {
        Customer customer = new Customer("Max Mustermann", "Musterstraße 1");
        UUID reservationUUID = UUID.randomUUID();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Reservation(reservationUUID, null, customer, 10)
        );
        assertEquals("Event darf nicht null sein", exception.getMessage());
    }

    @Test
    void reservationWithNullCustomer() {
        Event event = new Event(UUID.randomUUID(), "Konzert", new Date(), 50.0, 100);
        UUID reservationUUID = UUID.randomUUID();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Reservation(reservationUUID, event, null, 10)
        );
        assertEquals("Kunde darf nicht null sein", exception.getMessage());
    }

    @Test
    void reservationWithZeroReservedSeats() {
        Customer customer = new Customer("Max Mustermann", "Musterstraße 1");
        Event event = new Event(UUID.randomUUID(), "Konzert", new Date(), 50.0, 100);
        UUID reservationUUID = UUID.randomUUID();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Reservation(reservationUUID, event, customer, 0)
        );
        assertEquals("Reservierte Plätze müssen größer als null sein", exception.getMessage());
    }

    @Test
    void reservationWithTooManyReservedSeats() {
        Customer customer = new Customer("Max Mustermann", "Musterstraße 1");
        Event event = new Event(UUID.randomUUID(), "Konzert", new Date(), 50.0, 100);
        UUID reservationUUID = UUID.randomUUID();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Reservation(reservationUUID, event, customer, 150)
        );
        assertEquals("Reservierte Plätze dürfen nicht größer als verfügbare Sitzplatzanzahl sein.", exception.getMessage());
    }


}