package com.example.ticketingproject.domain.like.controller;


import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.like.dto.LikeResponse;
import com.example.ticketingproject.domain.like.service.LikeService;
import com.example.ticketingproject.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.ticketingproject.common.enums.SuccessStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/works/{workId}/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<CommonResponse<LikeResponse>> create(@PathVariable(name = "workId") Long workId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success(CREATE_SUCCESS, likeService.save(workId, customUserDetails.getId())));
    }

    @DeleteMapping("/{likeId}")
    public ResponseEntity<CommonResponse<LikeResponse>> delete(@PathVariable(name = "workId") Long workId, @PathVariable(name = "likeId") Long likeId,  @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(CommonResponse.success(DELETE_SUCCESS, likeService.delete(workId, likeId, customUserDetails.getId())));
    }
}
