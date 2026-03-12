package com.example.ticketingproject.domain.castmember.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.castmember.dto.CastMemberRequest;
import com.example.ticketingproject.domain.castmember.service.AdminCastMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.ticketingproject.common.enums.SuccessStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/performances/{performanceId}/sessions/{sessionId}/casts")
public class AdminCastMemberController {

    private final AdminCastMemberService adminCastMemberService;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> create(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId,
            @RequestBody CastMemberRequest request
    ) {
        adminCastMemberService.createCastMember(sessionId, request);
        return ResponseEntity.ok(CommonResponse.success(CREATE_SUCCESS, null));
    }

    @PatchMapping("/{castId}")
    public ResponseEntity<CommonResponse<Void>> update(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId,
            @PathVariable Long castId,
            @RequestBody CastMemberRequest request
    ) {
        adminCastMemberService.updateCastMember(castId, request);
        return ResponseEntity.ok(CommonResponse.success(UPDATE_SUCCESS, null));
    }

    @DeleteMapping("/{castId}")
    public ResponseEntity<CommonResponse<Void>> delete(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId,
            @PathVariable Long castId
    ) {
        adminCastMemberService.deleteCastMember(castId);
        return ResponseEntity.ok(CommonResponse.success(DELETE_SUCCESS, null));
    }

}