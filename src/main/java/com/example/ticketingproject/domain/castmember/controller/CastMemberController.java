package com.example.ticketingproject.domain.castmember.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.castmember.dto.CastMemberResponse;
import com.example.ticketingproject.domain.castmember.service.CastMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<CommonResponse<Page<CastMemberResponse>>> getPages(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(defaultValue = "1") int page
    ) {
        Pageable converted = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());
        Page<CastMemberResponse> response = castMemberService.getCastMembers(sessionId, converted);
        return ResponseEntity.ok(CommonResponse.success(READ_SUCCESS, response));
    }

}