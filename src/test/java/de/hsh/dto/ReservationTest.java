package de.hsh.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    @Test
    void validReservation() {
        Customer customer = new Customer("Max Mustermann", "Musterstraße 1");
        Event event = new Event(UUID.randomUUID(), "Konzert", new Date(), 50.0, 100, "organizer@mail.com");
        UUID reservationUUID = UUID.randomUUID();

        Reservation reservation = new Reservation(reservationUUID, event, customer, 10);

        assertNotNull(reservation);
        assertEquals(reservationUUID, reservation.uuid());
        assertEquals(event, reservation.event());
        assertEquals(customer, reservation.customer());
        assertEquals(10, reservation.reservedSeats());
    }

    @ParameterizedTest
    @DisplayName("Test für verschiedene ungültige Reservationen")
    @MethodSource("provideReservationData")
    void testReservation(UUID reservationUUID, Event event, Customer customer, int reservedSeats, String expectedMessage) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Reservation(reservationUUID, event, customer, reservedSeats)
        );
        assertEquals(expectedMessage, exception.getMessage());
    }

    // MethodSource für alle ungültigen Reservationen
    static Stream<Arguments> provideReservationData() {
        Customer customer = new Customer("Max Mustermann", "Musterstraße 1");
        Event event = new Event(UUID.randomUUID(), "Konzert", new Date(), 50.0, 100, "organizer@mail.com");
        UUID reservationUUID = UUID.randomUUID();

        return Stream.of(
                // Test für null UUID
                org.junit.jupiter.params.provider.Arguments.of(null, event, customer, 10, "UUID darf nicht null sein"),
                // Test für null Event
                org.junit.jupiter.params.provider.Arguments.of(reservationUUID, null, customer, 10, "Event darf nicht null sein"),
                // Test für null Customer
                org.junit.jupiter.params.provider.Arguments.of(reservationUUID, event, null, 10, "Kunde darf nicht null sein"),
                // Test für 0 reservierte Plätze
                org.junit.jupiter.params.provider.Arguments.of(reservationUUID, event, customer, 0, "Reservierte Plätze müssen größer als null sein"),
                // Test für zu viele reservierte Plätze
                org.junit.jupiter.params.provider.Arguments.of(reservationUUID, event, customer, 150, "Reservierte Plätze dürfen nicht größer als verfügbare Sitzplatzanzahl sein.")
        );
    }


}