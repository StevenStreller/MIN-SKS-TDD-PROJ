package de.hsh.service;

import de.hsh.dto.Customer;

import java.io.*;
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

    public void serializeCustomers(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(customers);
        } catch (IOException e) {
            throw new RuntimeException("Serialisierung fehlgeschlagen");
        }
    }

    @SuppressWarnings("unchecked")
    public void deserializeCustomers(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            List<Customer> deserializedCustomers = (List<Customer>) in.readObject();
            customers.clear();
            customers.addAll(deserializedCustomers);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Deserialisierung fehlgeschlagen");
        }
    }

}
