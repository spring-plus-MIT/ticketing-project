package com.example.ticketingproject.domain.seatgrade.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.seatgrade.dto.CreateSeatGradeRequest;
import com.example.ticketingproject.domain.seatgrade.dto.PutSeatGradeRequest;
import com.example.ticketingproject.domain.seatgrade.dto.SeatGradeResponse;
import com.example.ticketingproject.domain.seatgrade.service.AdminSeatGradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.ticketingproject.common.enums.SuccessStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/sessions/{sessionId}/seat-grades")
public class AdminSeatGradeController {

    private final AdminSeatGradeService adminSeatGradeService;

    @PostMapping()
    public ResponseEntity<CommonResponse<SeatGradeResponse>> create(@Valid @PathVariable(name = "sessionId") Long sessionId, CreateSeatGradeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success(CREATE_SUCCESS, CREATE_SUCCESS.getSuccessCode(), CREATE_SUCCESS.getMessage(), adminSeatGradeService.save(sessionId, request)));
    }

    @PutMapping("/{seatGradeId}")
    public ResponseEntity<CommonResponse<SeatGradeResponse>> update(@Valid @PathVariable(name = "sessionId") Long sessionId, @PathVariable(name = "seatGradeId") Long seatGradeId, PutSeatGradeRequest request) {
        return ResponseEntity.ok(CommonResponse.success(UPDATE_SUCCESS, UPDATE_SUCCESS.getSuccessCode(), UPDATE_SUCCESS.getMessage(), adminSeatGradeService.update(sessionId, seatGradeId, request)));
    }

    @DeleteMapping("/{seatGradeId}")
    public ResponseEntity<CommonResponse<Void>> delete(@PathVariable(name = "sessionId") Long sessionId, @PathVariable(name = "seatGradeId") Long seatGradeId) {
        adminSeatGradeService.delete(sessionId, seatGradeId);
        return ResponseEntity.ok(CommonResponse.success(DELETE_SUCCESS, DELETE_SUCCESS.getSuccessCode(), DELETE_SUCCESS.getMessage(), null));
    }
}
