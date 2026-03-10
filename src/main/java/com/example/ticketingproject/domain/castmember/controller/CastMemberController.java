package com.example.ticketingproject.domain.castmember.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.castmember.dto.CastMemberRequest;
import com.example.ticketingproject.domain.castmember.dto.CastMemberResponse;
import com.example.ticketingproject.domain.castmember.service.CastMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/performances/{performanceId}/sessions/{sessionId}/casts")
public class CastMemberController {

    private final CastMemberService castMemberService;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> create(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId,
            @RequestBody CastMemberRequest request
    ) {
        castMemberService.createCastMember(sessionId, request);
        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.CREATED,
                        SuccessStatus.CREATED.getSuccessCode(),
                        SuccessStatus.CREATED.getMessage(),
                        null
                ));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<CastMemberResponse>>> getPages(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId
    ) {
        List<CastMemberResponse> response = castMemberService.getCastMembers(sessionId);
        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.GET_SUCCESS,
                        SuccessStatus.GET_SUCCESS.getSuccessCode(),
                        SuccessStatus.GET_SUCCESS.getMessage(),
                        response
                ));
    }

    @PatchMapping("/{castId}")
    public ResponseEntity<CommonResponse<Void>> update(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId,
            @PathVariable Long castId,
            @RequestBody CastMemberRequest request
    ) {
        castMemberService.updateCastMember(castId, request);
        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.PROCESS_SUCCESS,
                        SuccessStatus.PROCESS_SUCCESS.getSuccessCode(),
                        SuccessStatus.PROCESS_SUCCESS.getMessage(),
                        null
                ));
    }

    @DeleteMapping("/{castId}")
    public ResponseEntity<CommonResponse<Void>> delete(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId,
            @PathVariable Long castId
    ) {
        castMemberService.deleteCastMember(castId);
        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.PROCESS_SUCCESS,
                        SuccessStatus.PROCESS_SUCCESS.getSuccessCode(),
                        SuccessStatus.PROCESS_SUCCESS.getMessage(),
                        null
                ));
    }
}