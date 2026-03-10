package com.example.ticketingproject.domain.venue.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.venue.dto.CreateVenueRequest;
import com.example.ticketingproject.domain.venue.dto.CreateVenueResponse;
import com.example.ticketingproject.domain.venue.service.AdminVenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/venues")
public class AdminVenueController {

    private final AdminVenueService adminVenueService;

    @PostMapping
    public ResponseEntity<CommonResponse<CreateVenueResponse>> createVenue(CreateVenueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.success(
                        SuccessStatus.CREATE_SUCCESS,
                        SuccessStatus.CREATE_SUCCESS.getSuccessCode(),
                        SuccessStatus.CREATE_SUCCESS.getMessage(),
                        adminVenueService.create(request)
                )
        );
    }
}
