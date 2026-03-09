package com.example.ticketingproject.domain.work.controller;

import com.example.ticketingproject.domain.work.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkController {
    private final WorkService workService;
}
