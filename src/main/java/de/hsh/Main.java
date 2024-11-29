package de.hsh;

import de.hsh.service.CustomerService;
import de.hsh.service.EventService;
import de.hsh.service.ReservationService;



public class Main {
    public static void main(String[] args) {
        init();
    }

    public static void init() {
        CustomerService customerService = new CustomerService();
        EventService eventService = new EventService();
        ReservationService reservationService = new ReservationService();

    }
}