package com.example.ticketingproject.domain.user.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.user.dto.GetUserResponse;
import com.example.ticketingproject.domain.user.dto.UpdateUserRequest;
import com.example.ticketingproject.domain.user.dto.UpdateUserResponse;
import com.example.ticketingproject.domain.user.service.UserService;
import com.example.ticketingproject.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<CommonResponse<GetUserResponse>> getOneUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getId();
        return ResponseEntity.ok(CommonResponse.success(SuccessStatus.READ_SUCCESS, userService.findOneUser(userId)));
    }

    @PutMapping("/me")
    public ResponseEntity<CommonResponse<UpdateUserResponse>> updateUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        Long userId = customUserDetails.getId();
        return ResponseEntity.ok(CommonResponse.success(SuccessStatus.UPDATE_SUCCESS, userService.updateUser(userId, request)));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<CommonResponse<Void>> withdrawUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Long userId = customUserDetails.getId();
        userService.withdrawUser(userId);
        return ResponseEntity.ok(CommonResponse.success(SuccessStatus.DELETE_SUCCESS, null));
    }
}
