package de.hsh.service;

import de.hsh.dto.Customer;
import de.hsh.dto.Event;
import de.hsh.dto.Reservation;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private ReservationService reservationService;
    private BlacklistService blacklistServiceMock;
    private EmailService emailServiceMock;
    private Customer customer1;
    private Customer customer2;
    private Event event;
    private Reservation reservation;

    @AfterAll
    static void tearDown() {
        String[] testFiles = {
                "reservations.ser",
                "empty_reservations.ser",
                "single_reservation.ser",
                "non_existent_file.ser",
                "corrupted_reservations.ser",
                "new_reservation.ser"
        };

        for (String filename : testFiles) {
            File file = new File(filename);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("Datei entfernt: " + filename);
                } else {
                    System.out.println("Fehler beim Löschen der Datei: " + filename);
                }
            }
        }
    }

    @BeforeEach
    void setUp() {
        blacklistServiceMock = mock(BlacklistService.class);
        emailServiceMock = mock(EmailService.class);

        reservationService = new ReservationService(blacklistServiceMock, emailServiceMock);
        customer1 = new Customer("Max Mustermann", "Musterstraße 1");
        customer2 = new Customer("Anna Müller", "Beispielstraße 2");
        event = new Event(UUID.randomUUID(), "Konzert", new java.util.Date(), 50.0, 100, "organizer@mail.com");
        reservation = new Reservation(UUID.randomUUID(), event, customer1, 10);
    }

    @Test
    void testEmailSentWhenMoreThan10PercentOfSeatsReserved() {
        // Angenommen, die Veranstaltung hat 100 Plätze und wir reservieren 20 Plätze
        Event event = new Event(UUID.randomUUID(), "Konzert", new java.util.Date(), 50.0, 100, "organizer@mail.com");
        event = new Event(event.identifier(), event.title(), event.date(), event.price(), event.totalSeats(), "organizer@mail.com");


        // Erstellung der Reservierung
        Customer customer = new Customer("Max Mustermann", "Musterstraße 1");
        Reservation reservation = new Reservation(UUID.randomUUID(), event, customer, 20);

        // Führen der Methode aus, die die E-Mail versendet
        reservationService.addReservation(reservation);

        // Überprüfen, ob die sendEmail-Methode des E-Mail-Dienstes aufgerufen wurde
        verify(emailServiceMock, times(1)).sendEmail(eq("organizer@mail.com"), eq("Buchung für Konzert bestätigt"),
                eq("Es wurden 20 Plätze für die Veranstaltung Konzert reserviert."));
    }

    @Test
    void testNoEmailSentWhenLessThan10PercentOfSeatsReserved() {
        // Angenommen, die Veranstaltung hat 100 Plätze und wir reservieren nur 5 Plätze (weniger als 10%)
        Event event = new Event(UUID.randomUUID(), "Konzert", new java.util.Date(), 50.0, 100, "organizer@mail.com");

        // Erstellung der Reservierung
        Customer customer = new Customer("Max Mustermann", "Musterstraße 1");
        Reservation reservation = new Reservation(UUID.randomUUID(), event, customer, 5);

        // Führen der Methode aus, die die E-Mail versendet
        reservationService.addReservation(reservation);

        // Überprüfen, dass keine E-Mail versendet wird, weil die Reservierung weniger als 10% der Plätze ausmacht
        verify(emailServiceMock, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testAddReservationWithBlacklistedCustomer() {
        // Setze das Verhalten des Mock-Blacklist-Dienstes
        when(blacklistServiceMock.isBlacklisted(customer1.name())).thenReturn(true);

        // Versuche, eine Buchung für einen blacklisted Kunden hinzuzufügen
        Reservation reservation = new Reservation(UUID.randomUUID(), event, customer1, 10);

        // Überprüfe, ob die erwartete Exception geworfen wird
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.addReservation(reservation);
        });

        assertEquals("Der Kunde befindet sich auf einer Blacklist und kann deshalb keine Buchung durchführen.", exception.getMessage());
    }

    @Test
    void getAvailableSeatsNoReservations() {
        // Keine Reservierungen, daher sollte die Anzahl der verfügbaren Plätze 100 sein
        int availableSeats = reservationService.getAvailableSeats(event);
        assertEquals(100, availableSeats, "Die verfügbaren Plätze sollten 100 sein, wenn keine Reservierungen existieren.");
    }

    @Test
    void getAvailableSeatsWithReservations() {
        // Füge einige Reservierungen hinzu
        reservationService.addReservation(new Reservation(UUID.randomUUID(), event, customer1, 30));
        reservationService.addReservation(new Reservation(UUID.randomUUID(), event, customer2, 21));

        // Die Summe der reservierten Plätze ist 30 + 21 = 51
        int availableSeats = reservationService.getAvailableSeats(event);
        assertEquals(49, availableSeats, "Die verfügbaren Plätze sollten 49 sein, wenn 30 und 21 Plätze reserviert sind.");
    }

    @Test
    void getAvailableSeatsNoExcessReservations() {
        // Füge Reservierungen hinzu, die die Gesamtzahl der Plätze nicht überschreiten
        reservationService.addReservation(new Reservation(UUID.randomUUID(), event, customer1, 40));
        reservationService.addReservation(new Reservation(UUID.randomUUID(), event, customer2, 40));

        // Die Summe der reservierten Plätze ist 40 + 40 = 80, was weniger als die verfügbaren 100 Plätze ist
        int availableSeats = reservationService.getAvailableSeats(event);
        assertEquals(20, availableSeats, "Die verfügbaren Plätze sollten 20 sein, wenn 40 und 40 Plätze reserviert sind.");
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
    void addReservationFailsWhenTooManySeatsReserved() {
        // Erste Reservierung: 50 Plätze
        Reservation reservation1 = new Reservation(UUID.randomUUID(), event, customer1, 50);
        reservationService.addReservation(reservation1);

        // Zweite Reservierung: 60 Plätze - Sollte fehlschlagen, da die Gesamtzahl die verfügbaren Plätze überschreitet (100)
        Reservation reservation2 = new Reservation(UUID.randomUUID(), event, customer2, 60);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservationService.addReservation(reservation2));

        assertEquals("Die Gesamtzahl der reservierten Plätze überschreitet die verfügbaren Plätze.", exception.getMessage());
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

        Event anotherEvent = new Event(UUID.randomUUID(), "Anderes Konzert", new java.util.Date(), 50.0, 100, "organizer@mail.com");
        Reservation retrievedReservation = reservationService.getReservation(anotherEvent, customer1);

        assertNull(retrievedReservation);
    }

    @Test
    void getReservationNotFoundForNonExistingEventAndCustomer() {
        Reservation reservation = new Reservation(UUID.randomUUID(), event, customer1, 10);
        reservationService.addReservation(reservation);

        Event nonExistingEvent = new Event(UUID.randomUUID(), "Anderes Konzert", new java.util.Date(), 50.0, 100, "organizer@mail.com");
        Customer nonExistingCustomer = new Customer("John Doe", "Unbekannte Straße");
        Reservation retrievedReservation = reservationService.getReservation(nonExistingEvent, nonExistingCustomer);

        assertNull(retrievedReservation);
    }

    @Test
    void serializationAndDeserialization() {
        when(blacklistServiceMock.isBlacklisted(customer1.name())).thenReturn(false);

        // Add reservation to the list
        reservationService.addReservation(reservation);

        // Serialize the reservation list
        String filename = "reservations.ser";
        reservationService.serializeReservations(filename);

        // Create a new ReservationService and deserialize the list
        ReservationService newReservationService = new ReservationService(blacklistServiceMock, emailServiceMock);
        newReservationService.deserializeReservations(filename);

        // Check that the deserialized list contains the same reservations
        List<Reservation> deserializedReservations = newReservationService.getReservations();
        assertEquals(1, deserializedReservations.size(), "Deserialized reservation list should have the same size as the original list");
        assertTrue(deserializedReservations.contains(reservation), "Deserialized list should contain the reservation");
    }

    @Test
    void serializationWithEmptyList() {
        when(blacklistServiceMock.isBlacklisted(customer1.name())).thenReturn(false);

        // Serialize an empty list
        String filename = "empty_reservations.ser";
        reservationService.serializeReservations(filename);

        // Deserialize into a new service
        ReservationService newReservationService = new ReservationService(blacklistServiceMock, emailServiceMock);
        newReservationService.deserializeReservations(filename);

        // Assert the list is still empty after deserialization
        assertTrue(newReservationService.getReservations().isEmpty(), "Deserialized reservation list should be empty for an empty list");
    }

    @Test
    void serializationWithOneReservation() {
        when(blacklistServiceMock.isBlacklisted(customer1.name())).thenReturn(false);

        // Add a single reservation and serialize the list
        reservationService.addReservation(reservation);
        String filename = "single_reservation.ser";
        reservationService.serializeReservations(filename);

        // Create a new ReservationService and deserialize
        ReservationService newReservationService = new ReservationService(blacklistServiceMock, emailServiceMock);
        newReservationService.deserializeReservations(filename);

        // Assert the deserialized list contains one reservation
        List<Reservation> deserializedReservations = newReservationService.getReservations();
        assertEquals(1, deserializedReservations.size(), "Deserialized reservation list should contain 1 reservation");
        assertTrue(deserializedReservations.contains(reservation), "Deserialized list should contain the reservation");
    }

    @Test
    void deserializationFileNotFound() {
        when(blacklistServiceMock.isBlacklisted(customer1.name())).thenReturn(false);

        String filename = "non_existent_file.ser";
        ReservationService newReservationService = new ReservationService(blacklistServiceMock, emailServiceMock);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> newReservationService.deserializeReservations(filename));
        assertEquals("Deserialisierung fehlgeschlagen", exception.getMessage(), "Deserialization should fail with the correct message when the file does not exist");
    }

    @Test
    void deserializationWithCorruptedFile() {
        when(blacklistServiceMock.isBlacklisted(customer1.name())).thenReturn(false);

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

            ReservationService newReservationService = new ReservationService(blacklistServiceMock, emailServiceMock);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> newReservationService.deserializeReservations(filename));
            assertEquals("Deserialisierung fehlgeschlagen", exception.getMessage(), "Deserialization should fail with the correct message when the file is corrupted");
        } catch (IOException e) {
            fail("Failed to create corrupted file: " + e.getMessage());
        }
    }

    @Test
    void serializationAndDeserializationWithDifferentReservationData() {
        when(blacklistServiceMock.isBlacklisted(customer1.name())).thenReturn(false);

        Reservation newReservation = new Reservation(UUID.randomUUID(), event, customer1, 20);

        // Serialize the new reservation
        String filename = "new_reservation.ser";
        reservationService.addReservation(newReservation);
        reservationService.serializeReservations(filename);

        // Create a new ReservationService and deserialize
        ReservationService newReservationService = new ReservationService(blacklistServiceMock, emailServiceMock);
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