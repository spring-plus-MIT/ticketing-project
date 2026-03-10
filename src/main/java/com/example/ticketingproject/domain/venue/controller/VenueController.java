package com.example.ticketingproject.domain.venue.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.venue.dto.VenueResponse;
import com.example.ticketingproject.domain.venue.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/venues")
public class VenueController {
    private final VenueService venueService;

    @GetMapping
    public ResponseEntity<CommonResponse<Page<VenueResponse>>> getVenues(
            @PageableDefault Pageable pageable,
            @RequestParam(defaultValue = "1") int page
    ) {
        Pageable converted = PageRequest.of(
                page - 1,
                pageable.getPageSize(),
                pageable.getSort()
        );
        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.READ_SUCCESS,
                        SuccessStatus.READ_SUCCESS.getSuccessCode(),
                        SuccessStatus.READ_SUCCESS.getMessage(),
                        venueService.getVenues(converted)
                )
        );
    }
}
