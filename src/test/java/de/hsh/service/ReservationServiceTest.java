package de.hsh.service;

import de.hsh.dto.Customer;
import de.hsh.dto.Event;
import de.hsh.dto.Reservation;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

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
    @DisplayName("Test: Merging multiple reservations with same customer and event")
    void mergeMultipleReservations() {

        Reservation firstReservation = new Reservation(UUID.randomUUID(), event, customer1, 10);
        Reservation secondReservation = new Reservation(UUID.randomUUID(), event, customer1, 15);
        Reservation thirdReservation = new Reservation(UUID.randomUUID(), event, customer1, 5);


        reservationService.addReservation(firstReservation);
        reservationService.addReservation(secondReservation);
        reservationService.addReservation(thirdReservation);


        assertEquals(1, reservationService.getReservations().size(), "Es sollte nur eine zusammengeführte Buchung vorhanden sein.");
        assertEquals(30, reservationService.getReservations().getFirst().reservedSeats(), "Die reservierten Plätze sollten korrekt summiert werden.");
        assertEquals(thirdReservation.uuid(), reservationService.getReservations().getFirst().uuid(), "Die ID der neuesten Buchung sollte übernommen werden.");
    }

    @Test
    @DisplayName("Überschreitung der Gesamtzahl der Sitzplätze führt zu einer IllegalArgumentException")
    void exceedingTotalSeatsThrowsException() {
        // Arrange
        Reservation firstReservation = new Reservation(UUID.randomUUID(), event, customer1, 90);
        Reservation secondReservation = new Reservation(UUID.randomUUID(), event, customer1, 15);

        // Act
        reservationService.addReservation(firstReservation);

        // Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservationService.addReservation(secondReservation), "Eine Exception sollte geworfen werden, wenn die reservierten Plätze die Gesamtanzahl übersteigen.");
        assertEquals("Die Gesamtzahl der reservierten Plätze überschreitet die verfügbaren Plätze.", exception.getMessage(), "Die Fehlermeldung sollte korrekt sein.");
    }


    @Test
    @DisplayName("Test: Customer with same name but different events should not merge reservations")
    void differentEventDoesNotMergeReservations() {
        // Arrange
        Event differentEvent = new Event(UUID.randomUUID(), "Oper", new java.util.Date(), 50.0, 100, "organizer2@mail.com");
        Reservation firstReservation = new Reservation(UUID.randomUUID(), event, customer1, 10);
        Reservation secondReservation = new Reservation(UUID.randomUUID(), differentEvent, customer1, 15);

        // Act
        reservationService.addReservation(firstReservation);
        reservationService.addReservation(secondReservation);

        // Assert
        assertEquals(2, reservationService.getReservations().size(), "Es sollten zwei separate Buchungen vorhanden sein.");
    }


    @ParameterizedTest
    @MethodSource("provideReservationsForEmailSending")
    @DisplayName("Test: E-Mail wird gesendet, wenn mehr als 10% der Plätze reserviert sind")
    void testEmailSendingBasedOnSeatsReserved(Event event, Customer customer, Reservation reservation, boolean shouldSendEmail) {
        reservationService.addReservation(reservation);

        if (shouldSendEmail) {
            verify(emailServiceMock, times(1)).sendEmail(eq("organizer@mail.com"), eq("Buchung für Konzert bestätigt"),
                    eq("Es wurden " + reservation.reservedSeats() + " Plätze für die Veranstaltung Konzert reserviert."));
        } else {
            verify(emailServiceMock, never()).sendEmail(anyString(), anyString(), anyString());
        }
    }

    private static Stream<Arguments> provideReservationsForEmailSending() {
        Event event = new Event(UUID.randomUUID(), "Konzert", new java.util.Date(), 50.0, 100, "organizer@mail.com");
        Customer customer = new Customer("Max Mustermann", "Musterstraße 1");

        Reservation reservationAboveThreshold = new Reservation(UUID.randomUUID(), event, customer, 20); // 20% reserved
        Reservation reservationBelowThreshold = new Reservation(UUID.randomUUID(), event, customer, 5); // 5% reserved

        return Stream.of(
                Arguments.of(event, customer, reservationAboveThreshold, true),
                Arguments.of(event, customer, reservationBelowThreshold, false)
        );
    }

    @Test
    void addReservationWithBlacklistedCustomer() {
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
    void addReservationWithNonBlacklistedCustomer() {
        // Setze das Verhalten des Mock-Blacklist-Dienstes
        when(blacklistServiceMock.isBlacklisted(customer1.name())).thenReturn(false);

        // Versuche, eine Buchung für einen non-blacklisted Kunden hinzuzufügen
        Reservation reservation = new Reservation(UUID.randomUUID(), event, customer1, 10);

        reservationService.addReservation(reservation);

        assertEquals(1, reservationService.getReservations().size());
    }

    @Test
    void availableSeatsNoReservations() {
        // Keine Reservierungen, daher sollte die Anzahl der verfügbaren Plätze 100 sein
        int availableSeats = reservationService.getAvailableSeats(event);
        assertEquals(100, availableSeats, "Die verfügbaren Plätze sollten 100 sein, wenn keine Reservierungen existieren.");
    }

    @Test
    void availableSeatsWithReservations() {
        // Füge einige Reservierungen hinzu
        reservationService.addReservation(new Reservation(UUID.randomUUID(), event, customer1, 30));
        reservationService.addReservation(new Reservation(UUID.randomUUID(), event, customer2, 21));

        // Die Summe der reservierten Plätze ist 30 + 21 = 51
        int availableSeats = reservationService.getAvailableSeats(event);
        assertEquals(49, availableSeats, "Die verfügbaren Plätze sollten 49 sein, wenn 30 und 21 Plätze reserviert sind.");
    }

    @Test
    void availableSeatsNoExcessReservations() {
        // Füge Reservierungen hinzu, die die Gesamtzahl der Plätze nicht überschreiten
        reservationService.addReservation(new Reservation(UUID.randomUUID(), event, customer1, 40));
        reservationService.addReservation(new Reservation(UUID.randomUUID(), event, customer2, 40));

        // Die Summe der reservierten Plätze ist 40 + 40 = 80, was weniger als die verfügbaren 100 Plätze ist
        int availableSeats = reservationService.getAvailableSeats(event);
        assertEquals(20, availableSeats, "Die verfügbaren Plätze sollten 20 sein, wenn 40 und 40 Plätze reserviert sind.");
    }


    @Test
    void getReservation() {
        Reservation reservation = new Reservation(UUID.randomUUID(), event, customer1, 10);

        reservationService.addReservation(reservation);

        Reservation retrievedReservation = reservationService.getReservation(event, customer1);

        assertNotNull(retrievedReservation);
        assertEquals(reservation, retrievedReservation);
    }

    @Test
    void reservationFailsWhenTooManySeatsReserved() {
        // Erste Reservierung: 50 Plätze
        Reservation reservation1 = new Reservation(UUID.randomUUID(), event, customer1, 50);
        reservationService.addReservation(reservation1);

        // Zweite Reservierung: 60 Plätze - Sollte fehlschlagen, da die Gesamtzahl die verfügbaren Plätze überschreitet (100)
        Reservation reservation2 = new Reservation(UUID.randomUUID(), event, customer2, 60);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservationService.addReservation(reservation2));

        assertEquals("Die Gesamtzahl der reservierten Plätze überschreitet die verfügbaren Plätze.", exception.getMessage());
    }


    @Test
    void reservationNotFound() {
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
    void deserializationFileNotFound() {
        when(blacklistServiceMock.isBlacklisted(customer1.name())).thenReturn(false);

        String filename = "non_existent_file.ser";
        ReservationService newReservationService = new ReservationService(blacklistServiceMock, emailServiceMock);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> newReservationService.deserializeReservations(filename));
        assertEquals("Deserialisierung fehlgeschlagen", exception.getMessage(), "Deserialization should fail with the correct message when the file does not exist");
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