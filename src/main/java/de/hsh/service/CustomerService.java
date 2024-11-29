package de.hsh.service;

import de.hsh.dto.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomerService {

    private final List<Customer> customers = new ArrayList<>();

    public List<Customer> getCustomers() {
        return customers;
    }

    public void addCustomer(Customer customer) {
        for (Customer c : customers) {
            if (Objects.equals(c.name(), customer.name())) {
                throw new IllegalArgumentException("Duplicate customer name: " + customer.name());
            }
        }
        customers.add(customer);
    }


}
