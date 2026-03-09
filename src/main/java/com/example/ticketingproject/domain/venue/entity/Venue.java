package com.example.ticketingproject.domain.venue.entity;

import com.example.ticketingproject.domain.venue.dto.VenueUpdateRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "venues")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Venue extends DeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(min = 1, max = 100)
    private String name;

    @NotBlank
    private String address;

    private int totalSeats;

    @Builder
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

    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.isDeleted() = true;
    }
}
