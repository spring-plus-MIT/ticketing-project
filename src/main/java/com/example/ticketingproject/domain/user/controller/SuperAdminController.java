package com.example.ticketingproject.domain.user.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.user.dto.UpdateUserRequest;
import com.example.ticketingproject.domain.user.dto.UpdateUserResponse;
import com.example.ticketingproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/super/admin")
public class SuperAdminController {

    private final UserService userService;

    @PostMapping("/{adminId}")
    public ResponseEntity<CommonResponse<Void>> activateAdmin(
            @PathVariable Long adminId
    ) {
        userService.activateUser(adminId);
        return ResponseEntity.ok(CommonResponse.success(SuccessStatus.UPDATE_SUCCESS, null));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<CommonResponse<UpdateUserResponse>> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(CommonResponse.success(
                SuccessStatus.UPDATE_SUCCESS, userService.updateUser(userId, request)));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<CommonResponse<Void>> withdrawUser(
            @PathVariable Long userId
    ) {
        userService.withdrawUser(userId);
        return ResponseEntity.ok(CommonResponse.success(SuccessStatus.DELETE_SUCCESS, null));
    }

}
