package com.example.ticketingproject.common.search.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.search.service.SearchRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.ticketingproject.common.enums.SuccessStatus.READ_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchRankingController {

    private final SearchRankingService searchRankingService;

    @GetMapping("/popular")
    public ResponseEntity<CommonResponse<List<String>>> getPopularKeyWords(
            @RequestParam(defaultValue = "performance") String domain
    ) {
        return ResponseEntity.ok(CommonResponse.success(READ_SUCCESS, searchRankingService.getRealTimeKeywords(domain)));
    }
}
