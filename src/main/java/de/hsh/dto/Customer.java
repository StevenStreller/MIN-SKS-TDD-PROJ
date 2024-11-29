package de.hsh.dto;

public record Customer(String name, String address) {

    public Customer {
        if (name == null) {
            throw new IllegalArgumentException("Name darf nicht null sein");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name darf nicht leer sein");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException("Name zu lang. Maximal 255 Zeichen");
        }

        if (address == null) {
            throw new IllegalArgumentException("Adresse darf nicht null sein");
        }
        if (address.isEmpty()) {
            throw new IllegalArgumentException("Adresse darf nicht leer sein");
        }
        if (address.length() > 255) {
            throw new IllegalArgumentException("Adresse zu lang. Maximal 255 Zeichen");
        }
    }
}
