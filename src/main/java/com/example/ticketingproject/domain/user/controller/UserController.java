package com.example.ticketingproject.domain.user.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.user.dto.GetUserResponse;
import com.example.ticketingproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<CommonResponse<Page<GetUserResponse>>> getUsers(
            @PageableDefault Pageable pageable,
            @RequestParam(defaultValue = "1") int page
            ) {
        Pageable converted = PageRequest.of(
                page - 1,
                pageable.getPageSize(),
                pageable.getSort()
        );
        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.FOUND_SUCCESS,
                        SuccessStatus.FOUND_SUCCESS.getSuccessCode(),
                        SuccessStatus.FOUND_SUCCESS.getMessage(),
                        userService.findAllUser(converted)
                        )
        );
    }

    @PostMapping("/withdraw/{userId}")
    public ResponseEntity<CommonResponse<Void>> withdrawUser(@PathVariable Long userId) {
        userService.withdrawUser(userId);
        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.DELETE_SUCCESS,
                        SuccessStatus.DELETE_SUCCESS.getSuccessCode(),
                        SuccessStatus.DELETE_SUCCESS.getMessage(),
                        null
                )
        );
    }

    @GetMapping("/deleted")
    public ResponseEntity<CommonResponse<Page<GetUserResponse>>> getDeletedUsers(
            @PageableDefault Pageable pageable,
            @RequestParam(defaultValue = "1") int page
    ) {
        Pageable converted = PageRequest.of(
                page - 1,
                pageable.getPageSize(),
                pageable.getSort()
        );
        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.FOUND_SUCCESS,
                        SuccessStatus.FOUND_SUCCESS.getSuccessCode(),
                        SuccessStatus.FOUND_SUCCESS.getMessage(),
                        userService.findAllDeletedUser(converted)
                )
        );
    }
}
