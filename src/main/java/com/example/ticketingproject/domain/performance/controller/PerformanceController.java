package com.example.ticketingproject.domain.performance.controller;

import com.example.ticketingproject.domain.performance.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceController {

    private final PerformanceService performanceService;
}
