package de.hsh.service;

import de.hsh.dto.Customer;
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
        // Add customers to the list
        customerService.addCustomer(customer1);
        customerService.addCustomer(customer2);

        // Serialize the customer list
        String filename = "customers.ser";
        customerService.serializeCustomers(filename);

        // Create a new CustomerService and deserialize the list
        CustomerService newCustomerService = new CustomerService();
        newCustomerService.deserializeCustomers(filename);

        // Check that the deserialized list contains the same customers
        List<Customer> deserializedCustomers = newCustomerService.getCustomers();
        assertEquals(2, deserializedCustomers.size(), "Deserialized customer list should have the same size as the original list");
        assertTrue(deserializedCustomers.contains(customer1), "Customer 1 should be present in the deserialized list");
        assertTrue(deserializedCustomers.contains(customer2), "Customer 2 should be present in the deserialized list");
    }

    @Test
    void serializationWithEmptyList() {
        // Serialize an empty list
        String filename = "empty_customers.ser";
        customerService.serializeCustomers(filename);

        // Deserialize into a new service
        CustomerService newCustomerService = new CustomerService();
        newCustomerService.deserializeCustomers(filename);

        // Assert the list is still empty after deserialization
        assertTrue(newCustomerService.getCustomers().isEmpty(), "Deserialized customer list should be empty for an empty list");
    }

    @Test
    void serializationWithOneCustomer() {
        // Add a single customer and serialize the list
        customerService.addCustomer(customer1);
        String filename = "single_customer.ser";
        customerService.serializeCustomers(filename);

        // Create a new CustomerService and deserialize
        CustomerService newCustomerService = new CustomerService();
        newCustomerService.deserializeCustomers(filename);

        // Assert the deserialized list contains one customer
        List<Customer> deserializedCustomers = newCustomerService.getCustomers();
        assertEquals(1, deserializedCustomers.size(), "Deserialized customer list should contain 1 customer");
        assertTrue(deserializedCustomers.contains(customer1), "Deserialized list should contain the customer");
    }

    @Test
    void deserializationFileNotFound() {
        String filename = "non_existent_file.ser";
        CustomerService newCustomerService = new CustomerService();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> newCustomerService.deserializeCustomers(filename));
        assertEquals("Deserialisierung fehlgeschlagen", exception.getMessage(), "Deserialization should fail with the correct message when the file does not exist");
    }

    @Test
    void deserializationWithCorruptedFile() {
        String filename = "corrupted_customers.ser";
        File file = new File(filename);

        // Create a corrupted file
        try {
            if (file.exists()) file.delete();
            file.createNewFile();
            // Write some invalid content to the file (not a valid serialized object)
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
    void serializationAndDeserializationWithDifferentCustomerData() {
        Customer newCustomer = new Customer("Alice Wonderland", "Avenue 42");

        // Serialize the new customer
        String filename = "new_customer.ser";
        customerService.addCustomer(newCustomer);
        customerService.serializeCustomers(filename);

        // Create a new CustomerService and deserialize
        CustomerService newCustomerService = new CustomerService();
        newCustomerService.deserializeCustomers(filename);

        // Assert that the new customer is in the deserialized list
        List<Customer> deserializedCustomers = newCustomerService.getCustomers();
        assertEquals(1, deserializedCustomers.size(), "Deserialized list should contain the new customer");
        assertTrue(deserializedCustomers.contains(newCustomer), "Deserialized list should contain Alice Wonderland");
    }


}