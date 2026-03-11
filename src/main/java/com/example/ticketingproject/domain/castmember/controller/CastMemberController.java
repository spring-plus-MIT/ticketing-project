package com.example.ticketingproject.domain.castmember.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.castmember.dto.CastMemberResponse;
import com.example.ticketingproject.domain.castmember.service.CastMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.ticketingproject.common.enums.SuccessStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/performances/{performanceId}/sessions/{sessionId}/casts")
public class CastMemberController {

    private final CastMemberService castMemberService;


    @GetMapping
    public ResponseEntity<CommonResponse<List<CastMemberResponse>>> getPages(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId
    ) {
        List<CastMemberResponse> response = castMemberService.getCastMembers(sessionId);
        return ResponseEntity.ok(CommonResponse.success(READ_SUCCESS, response));
    }

}