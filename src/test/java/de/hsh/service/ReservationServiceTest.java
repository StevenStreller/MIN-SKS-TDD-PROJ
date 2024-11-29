package de.hsh.service;

import de.hsh.dto.Customer;
import de.hsh.dto.Event;
import de.hsh.dto.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {

    private ReservationService reservationService;
    private Customer customer1;
    private Customer customer2;
    private Event event;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService();
        customer1 = new Customer("Max Mustermann", "Musterstraße 1");
        customer2 = new Customer("Anna Müller", "Beispielstraße 2");
        event = new Event(UUID.randomUUID(), "Konzert", new java.util.Date(), 50.0, 100);
        reservation = new Reservation(UUID.randomUUID(), event, customer1, 10);
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

    @Test
    void serializationAndDeserialization() {
        // Add reservation to the list
        reservationService.addReservation(reservation);

        // Serialize the reservation list
        String filename = "reservations.ser";
        reservationService.serializeReservations(filename);

        // Create a new ReservationService and deserialize the list
        ReservationService newReservationService = new ReservationService();
        newReservationService.deserializeReservations(filename);

        // Check that the deserialized list contains the same reservations
        List<Reservation> deserializedReservations = newReservationService.getReservations();
        assertEquals(1, deserializedReservations.size(), "Deserialized reservation list should have the same size as the original list");
        assertTrue(deserializedReservations.contains(reservation), "Deserialized list should contain the reservation");
    }

    @Test
    void serializationWithEmptyList() {
        // Serialize an empty list
        String filename = "empty_reservations.ser";
        reservationService.serializeReservations(filename);

        // Deserialize into a new service
        ReservationService newReservationService = new ReservationService();
        newReservationService.deserializeReservations(filename);

        // Assert the list is still empty after deserialization
        assertTrue(newReservationService.getReservations().isEmpty(), "Deserialized reservation list should be empty for an empty list");
    }

    @Test
    void serializationWithOneReservation() {
        // Add a single reservation and serialize the list
        reservationService.addReservation(reservation);
        String filename = "single_reservation.ser";
        reservationService.serializeReservations(filename);

        // Create a new ReservationService and deserialize
        ReservationService newReservationService = new ReservationService();
        newReservationService.deserializeReservations(filename);

        // Assert the deserialized list contains one reservation
        List<Reservation> deserializedReservations = newReservationService.getReservations();
        assertEquals(1, deserializedReservations.size(), "Deserialized reservation list should contain 1 reservation");
        assertTrue(deserializedReservations.contains(reservation), "Deserialized list should contain the reservation");
    }

    @Test
    void deserializationFileNotFound() {
        String filename = "non_existent_file.ser";
        ReservationService newReservationService = new ReservationService();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> newReservationService.deserializeReservations(filename));
        assertEquals("Deserialisierung fehlgeschlagen", exception.getMessage(), "Deserialization should fail with the correct message when the file does not exist");
    }

    @Test
    void deserializationWithCorruptedFile() {
        String filename = "corrupted_reservations.ser";
        File file = new File(filename);

        // Create a corrupted file
        try {
            if (file.exists()) file.delete();
            file.createNewFile();
            // Write some invalid content to the file (not a valid serialized object)
            try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                writer.write("Invalid data");
            }

            ReservationService newReservationService = new ReservationService();

            RuntimeException exception = assertThrows(RuntimeException.class, () -> newReservationService.deserializeReservations(filename));
            assertEquals("Deserialisierung fehlgeschlagen", exception.getMessage(), "Deserialization should fail with the correct message when the file is corrupted");
        } catch (IOException e) {
            fail("Failed to create corrupted file: " + e.getMessage());
        }
    }

    @Test
    void serializationAndDeserializationWithDifferentReservationData() {
        Reservation newReservation = new Reservation(UUID.randomUUID(), event, customer1, 20);

        // Serialize the new reservation
        String filename = "new_reservation.ser";
        reservationService.addReservation(newReservation);
        reservationService.serializeReservations(filename);

        // Create a new ReservationService and deserialize
        ReservationService newReservationService = new ReservationService();
        newReservationService.deserializeReservations(filename);

        // Assert that the new reservation is in the deserialized list
        List<Reservation> deserializedReservations = newReservationService.getReservations();
        assertEquals(1, deserializedReservations.size(), "Deserialized list should contain the new reservation");
        assertTrue(deserializedReservations.contains(newReservation), "Deserialized list should contain the new reservation");
    }

    @Test
    void serializeReservationsThrowsRuntimeExceptionOnIOException() {
        // Create a file that is not writable, e.g., a read-only file.
        String filename = "reservations.ser";
        File file = new File(filename);

        try {
            if (file.exists()) file.delete();
            file.createNewFile();

            // Make the file read-only to simulate an IOException during serialization
            file.setReadOnly();

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                reservationService.serializeReservations(filename);
            });

            assertEquals("Serialisierung fehlgeschlagen", exception.getMessage(), "Expected RuntimeException with message 'Serialisierung fehlgeschlagen'");

        } catch (IOException e) {
            fail("Failed to create or modify file: " + e.getMessage());
        } finally {
            // Clean up: Make the file writable again and delete
            file.setWritable(true);
            file.delete();
        }
    }

}