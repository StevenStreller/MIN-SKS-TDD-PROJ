package de.hsh.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests für die Customer-Klasse")
class CustomerTest {

    @Test
    @DisplayName("Ein gültiger Kunde sollte erstellt werden")
    void validCustomerShouldBeCreated() {
        Customer customer = new Customer("John Doe", "123 Main Street");
        assertNotNull(customer);
        assertEquals("John Doe", customer.name());
        assertEquals("123 Main Street", customer.address());
    }

    @ParameterizedTest
    @DisplayName("Adresse darf nicht null oder leer sein und maximal 255 Zeichen lang sein")
    @MethodSource("provideInvalidAddresses")
    void addressShouldNotBeInvalid(String input, String expectedMessage) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Customer("John Doe", input));
        assertEquals(expectedMessage, exception.getMessage());
    }

    static Stream<Arguments> provideInvalidAddresses() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(null, "Adresse darf nicht null sein"),
                org.junit.jupiter.params.provider.Arguments.of("", "Adresse darf nicht leer sein"),
                org.junit.jupiter.params.provider.Arguments.of("A".repeat(256), "Adresse zu lang. Maximal 255 Zeichen")
        );
    }

    @ParameterizedTest
    @DisplayName("Name darf nicht null oder leer sein und maximal 255 Zeichen lang sein")
    @MethodSource("provideInvalidNames")
    void nameShouldNotBeInvalid(String input, String expectedMessage) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Customer(input, "123 Main Street"));
        assertEquals(expectedMessage, exception.getMessage());
    }

    static Stream<Arguments> provideInvalidNames() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(null, "Name darf nicht null sein"),
                org.junit.jupiter.params.provider.Arguments.of("", "Name darf nicht leer sein"),
                org.junit.jupiter.params.provider.Arguments.of("A".repeat(256), "Name zu lang. Maximal 255 Zeichen")
        );
    }
}