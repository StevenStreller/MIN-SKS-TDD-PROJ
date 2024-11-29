package de.hsh.service;

public interface EmailService {
    void sendEmail(String to, String subject, String message);
}
