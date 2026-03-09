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
        return ResponseEntity.status(SuccessStatus.CREATED.getHttpStatus())
                .body(CommonResponse.success(
                        SuccessStatus.CREATED,
                        SuccessStatus.CREATED.getSuccessCode(),
                        "출연진 등록이 완료되었습니다.",
                        null
                ));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<CastMemberResponse>>> getCasts(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId
    ) {
        List<CastMemberResponse> response = castMemberService.getCastMembers(sessionId);
        return ResponseEntity.ok(CommonResponse.success(
                SuccessStatus.GET_SUCCESS,
                SuccessStatus.GET_SUCCESS.getSuccessCode(),
                "출연진 목록 조회가 완료되었습니다.",
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
        return ResponseEntity.ok(CommonResponse.success(
                SuccessStatus.PROCESS_SUCCESS,
                SuccessStatus.PROCESS_SUCCESS.getSuccessCode(),
                "출연진 정보가 수정되었습니다.",
                null
        ));
    }

    @PostMapping("/{castId}/delete")
    public ResponseEntity<CommonResponse<Void>> delete(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId,
            @PathVariable Long castId
    ) {
        castMemberService.deleteCastMember(castId);
        return ResponseEntity.ok(CommonResponse.success(
                SuccessStatus.PROCESS_SUCCESS,
                SuccessStatus.PROCESS_SUCCESS.getSuccessCode(),
                "출연진 정보가 삭제되었습니다.",
                null
        ));
    }
}