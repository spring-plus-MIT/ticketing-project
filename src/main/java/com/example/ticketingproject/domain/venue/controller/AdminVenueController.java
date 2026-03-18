package com.example.ticketingproject.domain.venue.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.venue.dto.CreateVenueRequest;
import com.example.ticketingproject.domain.venue.dto.UpdateVenueRequest;
import com.example.ticketingproject.domain.venue.dto.VenueResponse;
import com.example.ticketingproject.domain.venue.service.AdminVenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/venues")
public class AdminVenueController {

    private final AdminVenueService adminVenueService;

    @PostMapping
    public ResponseEntity<CommonResponse<VenueResponse>> createVenue(@Valid @RequestBody CreateVenueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.success(SuccessStatus.CREATE_SUCCESS, adminVenueService.create(request)));
    }

    @PatchMapping("/{venueId}")
    public ResponseEntity<CommonResponse<VenueResponse>> updateVenue (
            @PathVariable Long venueId,
            @Valid @RequestBody UpdateVenueRequest request
    ) {
        return ResponseEntity.ok(CommonResponse.success(
                SuccessStatus.UPDATE_SUCCESS, adminVenueService.updateVenue(venueId, request)));
    }

    @DeleteMapping("/{venueId}")
    public ResponseEntity<CommonResponse<Void>> deleteVenue (@PathVariable Long venueId) {
        adminVenueService.deleteVenue(venueId);

        return ResponseEntity.ok(CommonResponse.success(SuccessStatus.DELETE_SUCCESS, null));
    }
}
