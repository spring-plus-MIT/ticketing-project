package com.example.ticketingproject.domain.seat.entity;

import com.example.ticketingproject.common.entity.CreatableEntity;
import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.seat.enums.SeatStatus;
import com.example.ticketingproject.domain.seat.exception.SeatException;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.venue.entity.Venue;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_LENGTH_ERROR;
import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_NULL_ERROR;

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
    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private Venue venue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_grade_id", nullable = false)
    private SeatGrade seatGrade;

    @Length(min = 1, max = 10)
    private String rowName;

    @Min(value = 1, message = MSG_VALIDATION_LENGTH_ERROR)
    @Max(value = 10, message = MSG_VALIDATION_LENGTH_ERROR)
    private int seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;

    @Builder
    public Seat(Venue venue, SeatGrade seatGrade, String rowName, int seatNumber, SeatStatus seatStatus) {
        this.venue = venue;
        this.seatGrade = seatGrade;
        this.rowName = rowName;
        this.seatNumber = seatNumber;
        this.seatStatus = seatStatus;
    }

    public void reserve() {
        switch (this.seatStatus) {
            case RESERVED -> throw new SeatException(ErrorStatus.ALREADY_RESERVED_SEAT.getHttpStatus(), ErrorStatus.ALREADY_RESERVED_SEAT);
            case SOLD -> throw new SeatException(ErrorStatus.ALREADY_SOLD_SEAT.getHttpStatus(), ErrorStatus.ALREADY_SOLD_SEAT);
        }

        this.seatStatus = SeatStatus.RESERVED;
    }

    public void release() {
        this.seatStatus = SeatStatus.AVAILABLE;
    }

    public void sold() {
        this.seatStatus = SeatStatus.SOLD;
    }
}
