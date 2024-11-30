package de.hsh.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @ParameterizedTest
    @DisplayName("Test für totalSeats und Preisvalidierung")
    @MethodSource("provideTestCases")
    void testEventProperties(int seats, double price, Integer expectedSeats, String expectedMessage) {
        if (expectedSeats != null) {
            // Test für totalSeats
            Event event = new Event(UUID.randomUUID(), "Test Event", new Date(), price, seats, "organizer@mail.com");
            assertEquals(expectedSeats, event.totalSeats(), "Die verfügbaren Plätze sollten korrekt zurückgegeben werden.");
        } else if (expectedMessage != null) {
            // Test für ungültige Werte
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Event(UUID.randomUUID(), "Test Event", new Date(), price, seats, "organizer@mail.com"));
            assertEquals(expectedMessage, exception.getMessage());
        }
    }

    static Stream<Arguments> provideTestCases() {
        return Stream.of(
                // Test totalSeats mit validen Werten
                Arguments.of(50, 100.0, 50, null),
                Arguments.of(30, 100.0, 30, null),
                Arguments.of(75, 120.0, 75, null),
                Arguments.of(0, 50.0, 0, null),
                // Test für ungültige Werte
                Arguments.of(-5, 100.0, null, "Die verfügbaren Plätze dürfen nicht negativ sein."),
                Arguments.of(50, -10.0, null, "Der Preis darf nicht negativ sein.")
        );
    }

}