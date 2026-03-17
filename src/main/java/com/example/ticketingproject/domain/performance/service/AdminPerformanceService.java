package com.example.ticketingproject.domain.performance.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.performance.dto.PerformanceRequest;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.exception.PerformanceException;
import com.example.ticketingproject.domain.performance.repository.PerformanceRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.exception.VenueException;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.exception.WorkException;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketingproject.common.enums.ErrorStatus.*;
import static com.example.ticketingproject.common.enums.ErrorStatus.PERFORMANCE_NOT_FOUND;
import static com.example.ticketingproject.common.enums.ErrorStatus.VENUE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminPerformanceService {

    private final PerformanceRepository performanceRepository;
    private final WorkRepository workRepository;
    private final VenueRepository venueRepository;

    public void createPerformance(PerformanceRequest request) {
        Work work = workRepository.findById(request.getWorkId())
                .orElseThrow(() -> new WorkException(HttpStatus.NOT_FOUND, ErrorStatus.WORK_NOT_FOUND));
        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new VenueException(HttpStatus.NOT_FOUND, ErrorStatus.VENUE_NOT_FOUND));

        Performance performance = Performance.builder()
                .work(work)
                .venue(venue)
                .season(request.getSeason())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus())
                .build();

        performanceRepository.save(performance);
    }

    @CacheEvict(value = "performanceSearch", allEntries = true)
    public void updatePerformance(Long performanceId, PerformanceRequest request) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new PerformanceException(PERFORMANCE_NOT_FOUND.getHttpStatus(), PERFORMANCE_NOT_FOUND));

        Work work = workRepository.findById(request.getWorkId())
                .orElseThrow(() -> new WorkException(WORK_NOT_FOUND.getHttpStatus(), WORK_NOT_FOUND));
        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new VenueException(VENUE_NOT_FOUND.getHttpStatus(), VENUE_NOT_FOUND));

        performance.update(
                work,
                venue,
                request.getSeason(),
                request.getStartDate(),
                request.getEndDate(),
                request.getStatus()
        );
    }

    @CacheEvict(value = "performanceSearch", allEntries = true)
    public void closePerformance(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new PerformanceException(PERFORMANCE_NOT_FOUND.getHttpStatus(), PERFORMANCE_NOT_FOUND));

        performance.close();
    }

}
