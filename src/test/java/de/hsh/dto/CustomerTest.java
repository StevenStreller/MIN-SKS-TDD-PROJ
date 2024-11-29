package de.hsh.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    @Test
    @DisplayName("Name darf nicht null sein")
    void nameShouldNotBeNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Customer(null, "123 Main Street"));
        assertEquals("Name darf nicht null sein", exception.getMessage());
    }

    @Test
    @DisplayName("Name darf nicht leer sein")
    void nameShouldNotBeEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Customer("", "123 Main Street"));
        assertEquals("Name darf nicht leer sein", exception.getMessage());
    }

    @Test
    @DisplayName("Name darf maximal 255 Zeichen lang sein")
    void nameShouldNotExceedMaxLength() {
        String longName = "A".repeat(256);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Customer(longName, "123 Main Street"));
        assertEquals("Name zu lang. Maximal 255 Zeichen", exception.getMessage());
    }

    @Test
    @DisplayName("Adresse darf nicht null sein")
    void addressShouldNotBeNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Customer("John Doe", null));
        assertEquals("Adresse darf nicht null sein", exception.getMessage());
    }

    @Test
    @DisplayName("Adresse darf nicht leer sein")
    void addressShouldNotBeEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Customer("John Doe", ""));
        assertEquals("Adresse darf nicht leer sein", exception.getMessage());
    }

    @Test
    @DisplayName("Adresse darf maximal 255 Zeichen lang sein")
    void addressShouldNotExceedMaxLength() {
        String longAddress = "A".repeat(256);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Customer("John Doe", longAddress));
        assertEquals("Adresse zu lang. Maximal 255 Zeichen", exception.getMessage());
    }
}