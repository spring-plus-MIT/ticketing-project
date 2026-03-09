package com.example.ticketingproject.domain.seat.entity;

import com.example.ticketingproject.common.entity.CreatableEntity;
import com.example.ticketingproject.common.enums.SeatGrade;
import com.example.ticketingproject.domain.venue.entity.Venue;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@Table(name = "seats")
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Seat extends CreatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private SeatGrade gradeName;

    @NotBlank
    @Length(max = 10)
    private String rowName;

    @NotBlank
    private int seatNumber;

    @Builder
    public Seat(Venue venue, SeatGrade gradeName, String rowName, int seatNumber) {
        this.venue = venue;
        this.gradeName = gradeName;
        this.rowName = rowName;
        this.seatNumber = seatNumber;
    }


}
