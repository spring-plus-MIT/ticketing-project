package com.example.ticketingproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TicketingProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketingProjectApplication.class, args);
    }

}
