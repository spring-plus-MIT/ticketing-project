package com.example.ticketingproject.domain.seat.dto;

import com.example.ticketingproject.common.enums.GradeName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
@Builder
public class CreateSeatRequest {

    @NotBlank(message = "")
    @Enumerated(EnumType.STRING)
    private GradeName gradeName;

    @NotBlank(message = "")
    @Length(min = 1, max = 10, message = "")
    private String rowName;

    @NotBlank(message = "")
    @Length(min = 1, max = 10, message = "")
    private int seatNumber;
}
