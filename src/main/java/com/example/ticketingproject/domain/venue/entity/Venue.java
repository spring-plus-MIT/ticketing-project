package com.example.ticketingproject.domain.venue.entity;

import com.example.ticketingproject.domain.venue.dto.VenueUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "venues")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Venue extends DeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private int totalSeats;
    private LocalDateTime deletedAt;

    public Venue(String name, String address, int totalSeats) {
        this.name = name;
        this.address = address;
        this.totalSeats = totalSeats;
    }

    public void update(VenueUpdateRequest request) {
        this.name = request.getName();
        this.address = request.getAddress();
        this.totalSeats = request.getTotalSeats();
    }
}
