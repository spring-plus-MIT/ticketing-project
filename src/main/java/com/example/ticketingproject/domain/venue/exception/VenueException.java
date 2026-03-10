package com.example.ticketingproject.domain.venue.exception;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class VenueException extends BaseException {
    public VenueException(HttpStatus status, ErrorStatus errorStatusCode)  {
        super(status, errorStatusCode);
    }
}
