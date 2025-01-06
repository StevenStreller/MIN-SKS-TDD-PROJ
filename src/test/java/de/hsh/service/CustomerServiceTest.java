package de.hsh.service;

import de.hsh.dto.Customer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {


    private CustomerService customerService;
    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService();
        customer1 = new Customer("John Doe", "1234 Elm Street");
        customer2 = new Customer("Jane Doe", "5678 Oak Avenue");
    }

    @AfterAll
    static void tearDown() {
        String[] testFiles = {
                "empty_customers.ser",
                "single_customer.ser",
                "non_existent_file.ser",
                "corrupted_customers.ser",
                "new_customer.ser"
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

    @Test
    void customersListIsEmptyInitially() {
        List<Customer> customers = customerService.getCustomers();
        assertTrue(customers.isEmpty(), "Customer list should be empty initially");
    }


    @Test
    void addCustomer() {
        Customer customer = new Customer("John Doe", "1234 Elm Street");
        customerService.addCustomer(customer);

        List<Customer> customers = customerService.getCustomers();
        assertFalse(customers.isEmpty(), "Customer list should not be empty after adding a customer");
        assertTrue(customers.contains(customer), "Customer should be present in the list after adding");

        Customer customer2 = new Customer("Jane Doe", "5678 Oak Avenue");
        customerService.addCustomer(customer2);
        assertTrue(customers.contains(customer2), "Der zweite Kunde sollte in der Liste enthalten sein");

    }

    @Test
    void addCustomerWithDuplicateNameThrowsException() {
        Customer customer1 = new Customer("John Doe", "1234 Elm Street");
        customerService.addCustomer(customer1);

        Customer customer2 = new Customer("John Doe", "5678 Oak Avenue");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> customerService.addCustomer(customer2));

        assertEquals("Duplicate customer name: John Doe", exception.getMessage(), "Exception message should indicate duplicate name");
    }


    @Test
    void serializationAndDeserialization() {
        // Kunden zur Liste hinzufügen
        customerService.addCustomer(customer1);
        customerService.addCustomer(customer2);

        // Serialisiere die Kundenliste
        String filename = "customers.ser";
        customerService.serializeCustomers(filename);

        // Erstelle eine neuen CustomerService und deserialisiere die Liste
        CustomerService newCustomerService = new CustomerService();
        newCustomerService.deserializeCustomers(filename);


        List<Customer> deserializedCustomers = newCustomerService.getCustomers();
        assertEquals(2, deserializedCustomers.size(), "Deserialized customer list should have the same size as the original list");
        assertTrue(deserializedCustomers.contains(customer1), "Customer 1 should be present in the deserialized list");
        assertTrue(deserializedCustomers.contains(customer2), "Customer 2 should be present in the deserialized list");
    }

    @Test
    void deserializationWithCorruptedFile() {
        String filename = "corrupted_customers.ser";
        File file = new File(filename);

        // Eine beschädigte Datei erstellen
        try {
            if (file.exists()) file.delete();
            file.createNewFile();

            try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                writer.write("Invalid data");
            }

            CustomerService newCustomerService = new CustomerService();

            RuntimeException exception = assertThrows(RuntimeException.class, () -> newCustomerService.deserializeCustomers(filename));
            assertEquals("Deserialisierung fehlgeschlagen", exception.getMessage(), "Deserialization should fail with the correct message when the file is corrupted");
        } catch (IOException e) {
            fail("Failed to create corrupted file: " + e.getMessage());
        }
    }

    @Test
    void serializeCustomersThrowsRuntimeExceptionOnIOException() {
        // Erstellen Sie eine Datei, die nicht beschreibbar ist, z. B. eine schreibgeschützte Datei.
        String filename = "customers.ser";
        File file = new File(filename);

        try {
            if (file.exists()) file.delete();
            file.createNewFile();

            // Die Datei schreibgeschützt machen, um eine IOException während der Serialisierung zu simulieren
            file.setReadOnly();

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                customerService.serializeCustomers(filename);
            });

            assertEquals("Serialisierung fehlgeschlagen", exception.getMessage(), "Expected RuntimeException with message 'Serialisierung fehlgeschlagen'");

        } catch (IOException e) {
            fail("Failed to create or modify file: " + e.getMessage());
        } finally {
            // Aufräumen: Die Datei wieder beschreibbar machen und löschen
            file.setWritable(true);
            file.delete();
        }
    }


}