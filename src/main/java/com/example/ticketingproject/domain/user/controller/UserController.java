package com.example.ticketingproject.domain.user.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.user.dto.GetUserResponse;
import com.example.ticketingproject.domain.user.dto.UpdateUserResponse;
import com.example.ticketingproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse<GetUserResponse>> getOneUser(@PathVariable Long userId) {
        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.READ_SUCCESS,
                        SuccessStatus.READ_SUCCESS.getSuccessCode(),
                        SuccessStatus.READ_SUCCESS.getMessage(),
                        userService.findOneUser(userId)
                )
        );
    }

    @PutMapping("/{userId}")
    public ResponseEntity<CommonResponse<UpdateUserResponse>> updateUser(@PathVariable Long userId) {
        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.UPDATE_SUCCESS,
                        SuccessStatus.UPDATE_SUCCESS.getSuccessCode(),
                        SuccessStatus.UPDATE_SUCCESS.getMessage(),
                        userService.updateUser(userId)
                )
        );
    }

    @DeleteMapping("/{userId}")
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
}
