package com.example.ticketingproject.domain.seatgrade.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.seatgrade.dto.SeatGradeResponse;
import com.example.ticketingproject.domain.seatgrade.service.SeatGradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.ticketingproject.common.enums.SuccessStatus.READ_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sessions/{sessionId}/seat-grades")
public class SeatGradeController {

    private final SeatGradeService seatGradeService;

    @GetMapping
    public ResponseEntity<CommonResponse<Page<SeatGradeResponse>>> getAll(
            @PathVariable(name = "sessionId") Long sessionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return ResponseEntity.ok(CommonResponse.success(READ_SUCCESS, seatGradeService.findAll(sessionId, pageable)));
    }

    @GetMapping("/{seatGradeId}")
    public ResponseEntity<CommonResponse<SeatGradeResponse>> getOne(
            @PathVariable(name = "sessionId") Long sessionId,
            @PathVariable(name = "seatGradeId") Long seatGradeId) {
        return ResponseEntity.ok(CommonResponse.success(READ_SUCCESS, seatGradeService.findOne(sessionId, seatGradeId)));
    }
}
