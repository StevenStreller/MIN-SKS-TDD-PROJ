package de.hsh.service;

import de.hsh.dto.Customer;
import de.hsh.dto.Event;
import de.hsh.dto.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {

    private ReservationService reservationService;
    private Customer customer1;
    private Customer customer2;
    private Event event;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService();
        customer1 = new Customer("Max Mustermann", "Musterstraße 1");
        customer2 = new Customer("Anna Müller", "Beispielstraße 2");
        event = new Event(UUID.randomUUID(), "Konzert", new java.util.Date(), 50.0, 100);
    }

    @Test
    void addAndGetReservation() {
        Reservation reservation = new Reservation(UUID.randomUUID(), event, customer1, 10);

        reservationService.addReservation(reservation);

        Reservation retrievedReservation = reservationService.getReservation(event, customer1);

        assertNotNull(retrievedReservation);
        assertEquals(reservation, retrievedReservation);
    }

    @Test
    void getReservationNotFound() {
        Reservation reservation = new Reservation(UUID.randomUUID(), event, customer1, 10);
        reservationService.addReservation(reservation);

        Reservation retrievedReservation = reservationService.getReservation(event, customer2);

        assertNull(retrievedReservation);
    }

    @Test
    void getReservationWithNonExistingEvent() {
        Reservation reservation = new Reservation(UUID.randomUUID(), event, customer1, 10);
        reservationService.addReservation(reservation);

        Event anotherEvent = new Event(UUID.randomUUID(), "Anderes Konzert", new java.util.Date(), 50.0, 100);
        Reservation retrievedReservation = reservationService.getReservation(anotherEvent, customer1);

        assertNull(retrievedReservation);
    }

    @Test
    void getReservationNotFoundForNonExistingEventAndCustomer() {
        Reservation reservation = new Reservation(UUID.randomUUID(), event, customer1, 10);
        reservationService.addReservation(reservation);

        Event nonExistingEvent = new Event(UUID.randomUUID(), "Anderes Konzert", new java.util.Date(), 50.0, 100);
        Customer nonExistingCustomer = new Customer("John Doe", "Unbekannte Straße");
        Reservation retrievedReservation = reservationService.getReservation(nonExistingEvent, nonExistingCustomer);

        assertNull(retrievedReservation);
    }


}