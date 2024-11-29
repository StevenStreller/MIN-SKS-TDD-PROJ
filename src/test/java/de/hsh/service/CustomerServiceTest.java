package de.hsh.service;

import de.hsh.dto.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {


    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService();
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


}