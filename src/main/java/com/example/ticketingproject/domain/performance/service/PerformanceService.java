package com.example.ticketingproject.domain.performance.service;

import com.example.ticketingproject.domain.performance.dto.PerformanceRequest;
import com.example.ticketingproject.domain.performance.dto.PerformanceResponse;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.performance.repository.PerformanceRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final WorkRepository workRepository;
    private final VenueRepository venueRepository;

    @Transactional
    public void createPerformance(PerformanceRequest request) {
        Work work = workRepository.findById(request.getWorkId())
                .orElseThrow(() -> new IllegalArgumentException("작품 정보를 찾을 수 없습니다."));
        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new IllegalArgumentException("공연장 정보를 찾을 수 없습니다."));

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

    public Page<PerformanceResponse> getPerformances(Pageable pageable) {
        return performanceRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    public PerformanceResponse getPerformanceDetail(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공연을 찾을 수 없습니다."));
        return convertToResponse(performance);
    }

    @Transactional
    public void updatePerformance(Long performanceId, PerformanceRequest request) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공연을 찾을 수 없습니다."));

        Work work = workRepository.findById(request.getWorkId())
                .orElseThrow(() -> new IllegalArgumentException("작품 정보를 찾을 수 없습니다."));
        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new IllegalArgumentException("공연장 정보를 찾을 수 없습니다."));

        performance.update(
                work,
                venue,
                request.getSeason(),
                request.getStartDate(),
                request.getEndDate(),
                request.getStatus()
        );
    }

    private PerformanceResponse convertToResponse(Performance p) {
        return PerformanceResponse.builder()
                .id(p.getId())
                .workTitle(p.getWork().getTitle())
                .venueName(p.getVenue().getName())
                .season(p.getSeason())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .status(p.getStatus())
                .build();
    }
}